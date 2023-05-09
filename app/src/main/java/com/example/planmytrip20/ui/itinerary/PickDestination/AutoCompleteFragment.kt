package com.example.planmytrip20.ui.itinerary.PickDestination

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.SelectedLocation
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class AutoCompleteFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_auto_complete, container, false)

        val itinereryViewModel = ViewModelProvider(this).get(ItineraryViewModel::class.java)


        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.searchFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)

//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                var selectedLocation =
//                    place.name?.let { place.latLng?.let { it1 ->
//                        SelectedLocation(it,
//                            it1, emptyList(), emptyList())
//                    } }
//                itinereryViewModel.setDestination(selectedLocation)
//            }
//
//            override fun onError(status: Status) {
//                // Handle error
//            }
//        })


        return view
    }

}