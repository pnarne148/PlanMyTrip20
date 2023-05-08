package com.example.planmytrip20.ui.itinerary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.planmytrip20.R
import com.example.planmytrip20.WebScrape.WikipediaApi
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.ui.itinerary.overview.OverviewFragment
import com.example.planmytrip20.ui.itinerary.tripDetails.TripPlanFragment
import com.google.android.material.appbar.AppBarLayout
import com.example.planmytrip20.databinding.FragmentItineraryBinding
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
    private lateinit var selectedLocation: ItineraryLocation


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
        arguments?.let { bundle ->
            val place = bundle.getString("place")
            val latLng = bundle.getParcelable<LatLng>("location")
            selectedLocation = bundle.getParcelable<ItineraryLocation>("selLocation")!!

            Log.d("ItineraryFragment", "place: $place")
            Log.d("ItineraryFragment", "latLng: $latLng")
            Log.d("ItineraryFragment", "selectedLocation: $selectedLocation")
        }

//        Log.d(TAG, "onCreateView: "+place)

        val itinereryViewModel =
            ViewModelProvider(this).get(ItineraryViewModel::class.java)

        itinereryViewModel.setDestination(selectedLocation)

        _binding = FragmentItineraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tabs = binding.tabs
        viewPager = binding.viewPager

        var appBarLayout = binding.appBar
        binding.titleBig.text = "Trip to ${selectedLocation.name}"
        binding.titleSmall.text = "Trip to ${selectedLocation.name}"
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
            val imageURL = selectedLocation.name?.let { WikipediaApi.getImageUrlFromWikipedia(it) }
            Log.d(TAG, "onCreateView: "+imageURL)
            val bitmap = Picasso.get().load(imageURL).resize(1000, 1000).get()
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
        binding.homeSmall.setOnClickListener{
            removeFragment()
        }
    }

    fun removeFragment(){
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Define your pager adapter class
    private inner class MyPagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            // Return the fragment for the corresponding tab position
            return when (position) {
                0 -> OverviewFragment(fragment)
                1 -> TripPlanFragment(fragment)
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
                    .commitAllowingStateLoss()
            }
        }
    }


}