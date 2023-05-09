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
import com.example.planmytrip20.databinding.FragmentProfileBinding
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

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        cameraCheckPermission()
 //       galleryCheckPermission()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            if (photoUrl!=null)
            {
                displayImageUrl(photoUrl.toString())
            }
            // You can now use the name, email, and photoUrl variables as needed
        }


        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let { data ->
                        val bitmap = data.extras?.get("data") as Bitmap
                        val uri = bitmap.toUri(requireContext())
                        uploadImageToFirebaseStorage(uri)
                        //we are using coroutine image loader (coil)
                    }
                }
            }

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let { data ->
                        data.data?.let { uri ->
                            uploadImageToFirebaseStorage(uri)
                        }
                    }
                }
            }



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

            transaction.replace(R.id.nav_host_fragment_activity_main, editProfileFragment)
            transaction.addToBackStack(null)
            transaction.setReorderingAllowed(true)
            transaction.commit()

        }




        //when you click on the image
        binding.profilePhoto.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(requireContext())
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf(
                "Select photo from Gallery",
                "Capture photo from Camera"
            )
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->
                when (which) {
                    0 -> galleryLauncher.launch(
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                    )
                    1 -> cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                }
            }
            pictureDialog.show()
        }

        return root
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }

    private fun cameraCheckPermission() {

        Dexter.withContext(requireContext())
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(object :
                MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            camera()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRotationalDialogForPermission()
                }

            }
            ).onSameThread().check()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun galleryCheckPermission() {

        Dexter.withContext(requireContext()).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    requireContext(),
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: com.karumi.dexter.listener.PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage(
                "It looks like you have turned off permissions"
                        + "required for this feature. It can be enable under App settings!!!"
            )

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun uploadImageToFirebaseStorage(uri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$fileName")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Display the image URL
                    displayImageUrl(downloadUrl.toString())

                    // Set the user photo URL in Firebase Authentication
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User photo URL updated.")
                                } else {
                                    Log.e(TAG, "Error updating user photo URL.", task.exception)
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun displayUserDetails(username: String, useremail: String ) {
        // Display the image URL in a TextView or any other desired way
        binding.tvName.text = username
        binding.email.text=useremail
        }
    }

    fun Bitmap.toUri(context: Context): Uri {
        val bytes = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, this, "tempImage", null)
        return Uri.parse(path)
    }

