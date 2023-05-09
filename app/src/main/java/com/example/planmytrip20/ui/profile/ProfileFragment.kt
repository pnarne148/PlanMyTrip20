package com.example.planmytrip20.ui.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.planmytrip.auth.LoginActivity
import com.example.planmytrip20.R
import com.example.planmytrip20.api.FirebaseHelper
import com.example.planmytrip20.classes.database
import com.example.planmytrip20.databinding.FragmentProfileBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.example.planmytrip20.ui.profile.TripAdapter.TripAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val itineraryViewModel =
            ViewModelProvider(this).get(ItineraryViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        populateUI()

        binding.signout.setOnClickListener {
            // Firebase sign out
            FirebaseAuth.getInstance().signOut()

            // Redirect the user to the login screen or some other appropriate activity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // Get the FragmentManager
            val fragmentManager = requireActivity().supportFragmentManager

            // Pop the back stack to close the current fragment
            fragmentManager.popBackStack()

        }

        binding.settings.setOnClickListener {


            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()

            // Create a new instance of EditProfileFragment
            val editProfileFragment = EditProfileFragment()
            editProfileFragment.setOnProfileUpdatedListener(object : EditProfileFragment.OnProfileUpdatedListener {
                override fun onProfileUpdated() {
                    populateUI()
                    // Reload ProfileFragment data here
                }
            })

            transaction.replace(R.id.nav_host_fragment_activity_main, editProfileFragment)
            transaction.addToBackStack(null)
            transaction.setReorderingAllowed(true)
            transaction.commit()

        }


        populateMyTrips()


        return binding.root
    }

    private fun populateMyTrips() {
        FirebaseHelper().getAllItineraries {
            Log.d("firebase", "populateMyTrips: "+it.size)

            if(it.size == 0){
                binding.itineraryListHeader.text = "There are currently no trips available. Start planning your trip to see your trips here."
            }
            else{
                binding.itineraryListHeader.text = "My Trips"
            }

            binding.tripsList.adapter = TripAdapter(it)
        }
    }


    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        populateUI()
        super.onResume()
    }


    private fun populateUI() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                displayImageUrl(photoUrl.toString())
            }
            val userid = user.uid
            // fetching user details from db
            database.db.collection("userDetails")
                .whereEqualTo("uid", userid)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document != null) {
                            val userName = document.getString("userName")
                            if (!userName.isNullOrEmpty()) {
                                binding.tvName.text = userName
                            }

                            val phoneNumber = document.getString("phoneNumber")
                            if (!phoneNumber.isNullOrEmpty()) {
                                binding.phoneNumber.text = phoneNumber
                            }

                            val email = document.getString("email")
                            if (!email.isNullOrEmpty()) {
                                binding.email.text = email
                            }

                            val emergencyContact = document.getString("emergencyContact")
                            if (!emergencyContact.isNullOrEmpty()) {
                                binding.sosPhoneNumber.text = emergencyContact
                            }

                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting userDetails.", exception)
                }
            // You can now use the name, email, and photoUrl variables as needed
        }
    }
    private fun displayImageUrl(url: String) {
        // Display the image URL in a TextView or any other desired way
        binding.profilePhoto.load(url) {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }
    }
    }

    fun Bitmap.toUri(context: Context): Uri {
        val bytes = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, this, "tempImage", null)
        return Uri.parse(path)
    }

