package com.example.planmytrip20.ui.itinerary.tripDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.classes.OpeningHours
import com.example.planmytrip20.databinding.FragmentTripPlanBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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
            ViewModelProvider(requireParentFragment())[ItineraryViewModel::class.java]

        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 1",
            "50 Winship Street", LatLng(42.9912, -72.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 5))

        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 3",
            "50 Winship Street ", LatLng(42.9912, -72.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 4))
        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 2 12312312312321321321321321323213213213213213213123123123213",
            "50 Winship Street", LatLng(42.9912, -72.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 3))
        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 4",
            "50 Winship Street", LatLng(42.9912, -72.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 2))
        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 7",
            "50 Winship Street", LatLng(42.9912, -72.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 2))

        locations.add(ItineraryLocation("test_id", "test_place_id", "Brighton MA 7",
            "50 Winship Street", LatLng(42.9912, -72.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 2))


        Log.d(TAG, "testing list: "+locations.size)

        var adapter = ChecklistRecyclerViewAdapter(itineraryViewModel, locations)

        _binding = FragmentTripPlanBinding.inflate(inflater, container, false)

        _binding!!.list.adapter = adapter

        return binding.root
    }
}