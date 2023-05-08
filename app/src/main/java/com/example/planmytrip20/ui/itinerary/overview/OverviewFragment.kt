package com.example.planmytrip20.ui.itinerary.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planmytrip20.databinding.FragmentOverviewBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itineraryViewModel =
            ViewModelProvider(requireParentFragment()).get(ItineraryViewModel::class.java)

        itineraryViewModel.destination.observe(viewLifecycleOwner, Observer {
            Log.d("itinerery", "onCreateView: "+it.address+"working")
        })
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        with(binding.cardItem) {
            layoutManager = LinearLayoutManager(context)

            val values = listOf("Notes", "Places to Visit", "Flights", "Hotels")
            adapter = OverviewListAdapter(context, itineraryViewModel, viewLifecycleOwner, values)
        }

        return root
    }
}