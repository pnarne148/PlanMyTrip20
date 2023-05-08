package com.example.planmytrip20.ui.itinerary.tripDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
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

class TripPlanFragment(private val viewModelOwner: ViewModelStoreOwner) : Fragment() {
    private var _binding: FragmentTripPlanBinding? = null
    private val binding get() = _binding!!
    val TAG = "Trip Plan Fragment"
    private lateinit var adapter: ChecklistRecyclerViewAdapter

    private var locations : MutableList<ItineraryLocation> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itineraryViewModel =
            ViewModelProvider(viewModelOwner).get(ItineraryViewModel::class.java)

        itineraryViewModel.chosenPlaces.observe(viewLifecycleOwner, Observer { newLocations ->
            if(newLocations.isNotEmpty()){
                locations = newLocations as MutableList<ItineraryLocation>
                adapter = ChecklistRecyclerViewAdapter(itineraryViewModel, locations)
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
                    val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
                    transaction.addToBackStack(null)
                    transaction.setReorderingAllowed(true)
                    transaction.commit()
                }
            }

            Log.d("itinerery", "onCreateView: ${locations.size} working tripPlan")
        })

        itineraryViewModel.destination.observe(viewLifecycleOwner, Observer {
            Log.d("itinerery", "onCreateView: " + it.address+"working tripPlan")
        })

        _binding = FragmentTripPlanBinding.inflate(inflater, container, false)

        _binding!!.optimizeRouteText.setOnClickListener {
            MaterialAlertDialogBuilder(it.context).setTitle("Alert")
                .setMessage("This feature is only available for PRO members. Please upgrade to premium")
                .setPositiveButton("OK") { dialog, which ->
                }.show()
        }

        return binding.root
    }
}
