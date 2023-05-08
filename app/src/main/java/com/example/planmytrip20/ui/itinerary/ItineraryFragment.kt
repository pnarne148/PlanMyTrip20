package com.example.planmytrip20.ui.itinerary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.planmytrip20.MainActivity
import com.example.planmytrip20.R
import com.example.planmytrip20.WebScrape.WikipediaApi
import com.example.planmytrip20.WebScrape.WikipediaScraper
import com.example.planmytrip20.ui.itinerary.overview.OverviewFragment
import com.example.planmytrip20.ui.itinerary.tripDetails.TripPlanFragment
import com.google.android.material.appbar.AppBarLayout
import com.example.planmytrip20.databinding.FragmentItineraryBinding
import com.example.planmytrip20.ui.home.HomeFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ItineraryFragment : Fragment() {

    var TAG = "Itinerery"
    private var _binding: FragmentItineraryBinding? = null
    private lateinit var tabs: TabLayout
    private lateinit var viewPager: ViewPager2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var place: String
        val location: LatLng
        if(arguments != null) {
            place = requireArguments().getString("place").toString()
            location = requireArguments().getParcelable<LatLng>("location")!!
            Log.d(TAG, "onCreateView: "+place)
        }
        else{
            place = "Paris"
            location = LatLng(48.8566, 2.3522)
        }

        Log.d(TAG, "onCreateView: "+place)

        val itinereryViewModel =
            ViewModelProvider(this).get(ItineraryViewModel::class.java)

        itinereryViewModel.setDestination(place, location)

        _binding = FragmentItineraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tabs = binding.tabs
        viewPager = binding.viewPager

        var appBarLayout = binding.appBar
        binding.titleBig.text = "Trip to $place"
        binding.titleSmall.text = "Trip to $place"
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) == appBarLayout.totalScrollRange) {
                binding.shareButton.visibility = View.VISIBLE
                binding.titleBig.visibility = View.GONE
                binding.titleSmall.visibility = View.VISIBLE
            } else {
                binding.shareButton.visibility = View.GONE
                binding.titleBig.visibility = View.VISIBLE
                binding.titleSmall.visibility = View.GONE
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            val imageURL = WikipediaApi.getImageUrlFromWikipedia(place)
            Log.d(TAG, "onCreateView: "+imageURL)
            val bitmap = Picasso.get().load(imageURL).get()
            withContext(Dispatchers.Main) {
                binding.placeBg.setImageBitmap(bitmap)
            }
        }

        handleHomeButton()

        // Set up the tabs and view pager
        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Overview"
                1 -> "Trip Plan"
                2 -> "Photos"
                else -> ""
            }
        }.attach()

        viewPager.isUserInputEnabled = false

        return root
    }

    private fun handleHomeButton() {
//        binding.homeBig.setOnClickListener{
//            removeFragment()
//        }

        binding.homeSmall.setOnClickListener{
            removeFragment()
        }
    }

    fun removeFragment(){
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
//            if (fragment != null) {
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .remove(fragment)
//                    .commit()
//            }
//        }
        requireActivity().onBackPressed()

//        (activity as MainActivity).openHome()
//        (activity as MainActivity).showBottomNavigation()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            if (fragment != null) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
    }


}