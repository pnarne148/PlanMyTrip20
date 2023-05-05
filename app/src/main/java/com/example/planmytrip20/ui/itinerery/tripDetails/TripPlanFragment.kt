package com.example.planmytrip20.ui.itinerery.tripDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.planmytrip20.R
import com.example.planmytrip20.databinding.FragmentItinereryBinding
import com.example.planmytrip20.databinding.FragmentOverviewBinding
import com.example.planmytrip20.databinding.FragmentTripPlanBinding
import com.example.planmytrip20.ui.itinerery.ItinereryViewModel


class TripPlanFragment : Fragment() {
    private var _binding: FragmentTripPlanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itinereryViewModel =
            ViewModelProvider(requireParentFragment()).get(ItinereryViewModel::class.java)

        _binding = FragmentTripPlanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        itinereryViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textSample.text = it
        })

        return root
    }
}