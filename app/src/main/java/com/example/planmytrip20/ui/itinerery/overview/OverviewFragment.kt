package com.example.planmytrip20.ui.itinerery.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.planmytrip20.databinding.FragmentItinereryBinding
import com.example.planmytrip20.databinding.FragmentOverviewBinding
import com.example.planmytrip20.ui.itinerery.ItinereryViewModel
import com.google.android.material.tabs.TabLayoutMediator

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itinereryViewModel =
            ViewModelProvider(requireParentFragment()).get(ItinereryViewModel::class.java)

        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
}