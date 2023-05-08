package com.example.planmytrip20.ui.itinerary.tripDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.classes.OpeningHours
import com.example.planmytrip20.databinding.FragmentTripPlanBinding
import com.example.planmytrip20.ui.common.WebViewFragment
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.example.planmytrip20.ui.itinerary.maps.MapBottomSheetFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class TripPlanFragment : Fragment() {
    private var _binding: FragmentTripPlanBinding? = null
    private val binding get() = _binding!!
    val TAG = "Trip Plan Fragment"

    private var locations : MutableList<ItineraryLocation> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itineraryViewModel =
            ViewModelProvider(requireParentFragment()).get(ItineraryViewModel::class.java)

        // TODO: Get all the locations that are in itinerary and pass to recyclerview
        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 1",
            "50 Winship Street", LatLng(42.9912, -70.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 5))

        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 3",
            "50 Winship Street ", LatLng(42.9912, -72.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 4))
        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 2 12312312312321321321321321323213213213213213213123123123213",
            "50 Winship Street", LatLng(42.9912, -74.456), OpeningHours(true), false, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 3))
        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 4",
            "50 Winship Street", LatLng(42.9912, -76.456), OpeningHours(true), false, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 2))
        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 7",
            "50 Winship Street", LatLng(42.9912, -78.456), OpeningHours(true), false, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 2))

        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 7",
            "50 Winship Street", LatLng(42.9912, -80.456), OpeningHours(true), false, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 2))


        Log.d(TAG, "testing list: "+locations.size)

        var adapter = ChecklistRecyclerViewAdapter(itineraryViewModel, locations)

        _binding = FragmentTripPlanBinding.inflate(inflater, container, false)

        _binding!!.optimizeRouteText.setOnClickListener {
            MaterialAlertDialogBuilder(it.context).setTitle("Alert")
                .setMessage("This feature is only available for PRO members. Please upgrade to premium")
                .setPositiveButton("OK") { dialog, which ->

                }.show()
        }


        _binding!!.list.adapter = adapter

        adapter.openMap = { loc1: ItineraryLocation, loc2: ItineraryLocation, mapType: String ->
            val modal = MapBottomSheetFragment()
            val bundle = Bundle()
            modal.dialog
            bundle.putString("source", GsonBuilder().create().toJson(loc1))
            bundle.putString("destination", GsonBuilder().create().toJson(loc2))
            bundle.putString("mapType", mapType)
            modal.arguments = bundle
            modal.show(childFragmentManager, "Map bottom sheet")
        }

        adapter.onCheckBoxChange = {position: Int, checked: Boolean ->
            Log.d(TAG, "Fragment onCheckBoxChange")
            if(checked){
                locations[position].visited = true
                if (!binding.list.isComputingLayout)
                {
                    //TODO: Update the database and cache with location update
                    adapter.notifyItemChanged(position)
                    adapter.notifyItemChanged(position + 1)
                    adapter.notifyItemChanged(position - 1)
                }
            } else {
                locations[position].visited = false
                if (!binding.list.isComputingLayout)
                {
                    //TODO: Update the database and cache with location update
                    adapter.notifyItemChanged(position)
                    adapter.notifyItemChanged(position + 1)
                    adapter.notifyItemChanged(position - 1)
                }
            }
        }

        adapter.openWebView = {wikiUrl: String ->
            var fragment = WebViewFragment()
//            val transaction = childFragmentManager.beginTransaction()
//            transaction.replace(R.id., fragment)
//            transaction.addToBackStack(null)
//            transaction.setReorderingAllowed(true)
//            transaction.commit()
        }

        return binding.root
    }
}
