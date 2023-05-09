package com.example.planmytrip20.ui.itinerary.tripDetails

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.FragmentTripPlanBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class TripPlanFragment(private val viewModelOwner: ViewModelStoreOwner) : Fragment() {
    private var _binding: FragmentTripPlanBinding? = null
    private val binding get() = _binding!!
    val TAG = "Trip Plan Fragment"
    private lateinit var adapter: ChecklistRecyclerViewAdapter
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private var position: Int = 0

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private fun startGalleryIntent(position: Int) {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        this.position = position
        cameraLauncher.launch(galleryIntent)
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d(TAG,"++++++++++++++++++++++++++++")
                    Log.d(TAG, "Image URL: $downloadUri")
                    // Save the URL to the location object or database, and update the UI accordingly
                    val itineraryViewModel = ViewModelProvider(viewModelOwner).get(ItineraryViewModel::class.java)
                    itineraryViewModel.chosenPlaces.observe(viewLifecycleOwner, Observer {newLocations ->
                        Log.d(TAG, "Photo Urls Before: ${newLocations[position].user_photo_urls?.size}")
                        newLocations[position].user_photo_urls = newLocations[position].user_photo_urls?.plus(downloadUri.toString())
                        Log.d(TAG, "Photo Urls After: ${newLocations[position].user_photo_urls?.size}")
//                        binding.list.adapter?.notifyItemChanged(position)
                    })
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to upload image: ${exception.message}")
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itineraryViewModel = ViewModelProvider(viewModelOwner).get(ItineraryViewModel::class.java)

        val binding = FragmentTripPlanBinding.inflate(inflater, container, false)


        with(binding.list) {
            layoutManager = LinearLayoutManager(context)

            itineraryViewModel.chosenPlaces.observe(viewLifecycleOwner, Observer {newLocations ->
                if(newLocations.isNotEmpty()) {
                    itineraryViewModel.latestIndex.observe(viewLifecycleOwner, Observer {
                        adapter = ChecklistRecyclerViewAdapter(context, itineraryViewModel, newLocations, it, ::startGalleryIntent)
                        binding.list.adapter = adapter
                        if(it > 1)
                            scrollToPosition(it-1)
                    })
                }
            })
        }

        binding.optimizeRouteText.setOnClickListener {
            MaterialAlertDialogBuilder(it.context).setTitle("Alert")
                .setMessage("This feature is only available for PRO members. Please upgrade to premium")
                .setPositiveButton("OK") { dialog, which ->
                }.show()
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val clipData = result.data?.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                    }
                } else {
                    val imageUri = result.data?.data
                    if (imageUri != null) {
                        uploadImageToFirebaseStorage(imageUri)
                    } else {
                        Log.e(TAG, "Image URI is null")
                    }
                }
            }
        }

        return binding.root
    }
}
