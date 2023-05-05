package com.example.planmytrip20.ui.itinerery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.planmytrip20.databinding.FragmentItinereryBinding
import com.example.planmytrip20.ui.itinerery.overview.OverviewFragment
import com.example.planmytrip20.ui.itinerery.tripDetails.TripPlanFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ItinereryFragment : Fragment() {

    private var _binding: FragmentItinereryBinding? = null
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
        val itinereryViewModel =
            ViewModelProvider(this).get(ItinereryViewModel::class.java)

        itinereryViewModel.setText("testing view model")
        _binding = FragmentItinereryBinding.inflate(inflater, container, false)
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

//        val textView: TextView = binding.textDashboard
//        itinereryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
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