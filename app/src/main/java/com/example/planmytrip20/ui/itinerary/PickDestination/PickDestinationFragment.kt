package com.example.planmytrip20.ui.itinerary.PickDestination

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.planmytrip20.MainActivity
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.classes.OpeningHours
import com.example.planmytrip20.classes.SelectedLocation
import com.example.planmytrip20.ui.itinerary.ItineraryFragment
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class PickDestinationFragment : Fragment() {

    private lateinit var view: View
    private lateinit var selectedLocation: ItineraryLocation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pick_destination, container, false)
        view.findViewById<ConstraintLayout>(R.id.pick_destination).visibility = View.VISIBLE

        val itinereryViewModel = ViewModelProvider(this).get(ItineraryViewModel::class.java)

        (activity as MainActivity).hideBottomNavigation()

        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.searchFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.RATING))
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedLocation = ItineraryLocation(0.toString(), place.id, place.name, place.address,  OpeningHours(true),place.latLng?.latitude!!,place.latLng?.longitude!!, false, "", "", place.rating, "", null, null)
                view.findViewById<Button>(R.id.startPlanning).isEnabled = true
            }

            override fun onError(status: Status) {
                // Handle error
            }
        })

        view.findViewById<ImageView>(R.id.go_back).setOnClickListener {
            (activity as MainActivity).openHome()
        }

        view.findViewById<Button>(R.id.startPlanning).setOnClickListener {
            val fragment = ItineraryFragment()

            val bundle = Bundle()
            bundle.putString("place", selectedLocation.address)
            bundle.putParcelable("location", selectedLocation.getLatLng())
            bundle.putParcelable("selLocation", selectedLocation)
            fragment.arguments = bundle


            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
            transaction.addToBackStack(null)
            transaction.setReorderingAllowed(true)
            transaction.commit()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.d("itinerery", "onResume: ")
    }

    override fun onPause() {
        super.onPause()
//        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onStop() {
        super.onStop()
//        (activity as MainActivity).showBottomNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomNavigation()
    }
}