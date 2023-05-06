package com.example.planmytrip20.ui.itinerary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.planmytrip20.databinding.FragmentItineraryBinding
import com.example.planmytrip20.ui.itinerary.overview.OverviewFragment
import com.example.planmytrip20.ui.itinerary.tripDetails.TripPlanFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ItineraryFragment : Fragment() {

    private var _binding: FragmentItineraryBinding? = null
    private lateinit var tabs: TabLayout
    private lateinit var viewPager: ViewPager2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itineraryViewModel =
            ViewModelProvider(this).get(ItineraryViewModel::class.java)

        itineraryViewModel.setText("testing view model")
        _binding = FragmentItineraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tabs = binding.tabs
        viewPager = binding.viewPager

        // Set up the tabs and view pager
        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Overview"
                1 -> "Trip Plan"
                else -> ""
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Define your pager adapter class
    private inner class MyPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            // Return the fragment for the corresponding tab position
            return when (position) {
                0 -> OverviewFragment()
                1 -> TripPlanFragment()
                else -> throw IllegalArgumentException("Invalid tab position")
            }
        }
    }
}