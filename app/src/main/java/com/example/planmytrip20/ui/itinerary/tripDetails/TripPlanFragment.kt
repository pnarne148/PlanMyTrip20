package com.example.planmytrip20.ui.itinerary.tripDetails

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.FragmentTripPlanBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.example.planmytrip20.ui.itinerary.maps.MapBottomSheetFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.GsonBuilder
import java.util.*
import kotlin.collections.ArrayList

class TripPlanFragment(private val viewModelOwner: ViewModelStoreOwner) : Fragment() {
    private var _binding: FragmentTripPlanBinding? = null
    private val binding get() = _binding!!
    val TAG = "Trip Plan Fragment"
    private lateinit var adapter: ChecklistRecyclerViewAdapter
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val STORAGE_PERMISSION_CODE = 1001
    private var locations : MutableList<ItineraryLocation> = ArrayList()

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private fun startGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
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
        val itineraryViewModel =
            ViewModelProvider(viewModelOwner).get(ItineraryViewModel::class.java)
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val clipData = result.data?.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        uploadImageToFirebaseStorage(imageUri)
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
        val binding = FragmentTripPlanBinding.inflate(inflater, container, false)
        with(binding.list) {
            layoutManager = LinearLayoutManager(context)

            itineraryViewModel.chosenPlaces.observe(viewLifecycleOwner, Observer {newLocations ->
                if(newLocations.isNotEmpty()) {
                    itineraryViewModel.latestIndex.observe(viewLifecycleOwner, Observer {
                        adapter = ChecklistRecyclerViewAdapter(context, itineraryViewModel, newLocations, it, ::startGalleryIntent)
                    })
                }
            })
        }



//        itineraryViewModel.chosenPlaces.observe(viewLifecycleOwner, Observer { newLocations ->
//            if(newLocations.isNotEmpty()){
//                locations = newLocations as MutableList<ItineraryLocation>
//                adapter = ChecklistRecyclerViewAdapter(context, itineraryViewModel, locations)
//                _binding!!.list.adapter = adapter
//
//                adapter.openMap = { loc1: ItineraryLocation, loc2: ItineraryLocation, mapType: String ->
//                    val modal = MapBottomSheetFragment()
//                    val bundle = Bundle()
//                    modal.dialog
//                    bundle.putString("source", GsonBuilder().create().toJson(loc1))
//                    bundle.putString("destination", GsonBuilder().create().toJson(loc2))
//                    bundle.putString("mapType", mapType)
//                    modal.arguments = bundle
//                    modal.show(childFragmentManager, "Map bottom sheet")
//                }
//
//                adapter.onCheckBoxChange = {position: Int, checked: Boolean ->
//                    Log.d(TAG, "Fragment onCheckBoxChange")
//                    if(checked){
//                        locations[position].visited = true
//                        if (!binding.list.isComputingLayout)
//                        {
//                            //TODO: Update the database and cache with location update
//                            adapter.notifyItemChanged(position)
//                            adapter.notifyItemChanged(position + 1)
//                            adapter.notifyItemChanged(position - 1)
//                        }
//                    } else {
//                        locations[position].visited = false
//                        if (!binding.list.isComputingLayout)
//                        {
//                            //TODO: Update the database and cache with location update
//                            adapter.notifyItemChanged(position)
//                            adapter.notifyItemChanged(position + 1)
//                            adapter.notifyItemChanged(position - 1)
//                        }
//                    }
//                }
//
//                adapter.openWebView = {wikiUrl: String ->
//                    var fragment = WebViewFragment()
//                    val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//                    transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
//                    transaction.addToBackStack(null)
//                    transaction.setReorderingAllowed(true)
//                    transaction.commit()
//                }
//            }
//
//            Log.d("itinerery", "onCreateView: ${locations.size} working tripPlan")
//        })
//
//        _binding = FragmentTripPlanBinding.inflate(inflater, container, false)
//
//        _binding!!.optimizeRouteText.setOnClickListener {
//            MaterialAlertDialogBuilder(it.context).setTitle("Alert")
//                .setMessage("This feature is only available for PRO members. Please upgrade to premium")
//                .setPositiveButton("OK") { dialog, which ->
//                }.show()
//        }

        return binding.root
    }
}
