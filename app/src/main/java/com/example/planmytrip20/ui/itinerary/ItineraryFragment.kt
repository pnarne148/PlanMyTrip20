package com.example.planmytrip20.ui.itinerary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.planmytrip20.R
import com.example.planmytrip20.WebScrape.WikipediaApi
import com.example.planmytrip20.WebScrape.WikipediaScraper
import com.example.planmytrip20.databinding.FragmentItinereryBinding
import com.example.planmytrip20.ui.itinerery.overview.OverviewFragment
import com.example.planmytrip20.ui.itinerery.tripDetails.TripPlanFragment
import com.google.android.material.appbar.AppBarLayout
import com.example.planmytrip20.databinding.FragmentItineraryBinding
import com.example.planmytrip20.ui.itinerary.overview.OverviewFragment
import com.example.planmytrip20.ui.itinerary.tripDetails.TripPlanFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ItineraryFragment : Fragment() {

    var TAG = "Itinerery"
    private var _binding: FragmentItinereryBinding? = null
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
        val place = "Paris"

        val itinereryViewModel =
            ViewModelProvider(this).get(ItinereryViewModel::class.java)

        itinereryViewModel.setText("testing view model")

        _binding = FragmentItinereryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tabs = binding.tabs
        viewPager = binding.viewPager

        var appBarLayout = binding.appBar
        binding.titleBig.text = "Trip to $place"
        binding.titleSmall.text = "Trip to $place"
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) == appBarLayout.totalScrollRange) {
                binding.homeButton.visibility = View.VISIBLE
                binding.shareButton.visibility = View.VISIBLE
                binding.titleBig.visibility = View.GONE
                binding.titleSmall.visibility = View.VISIBLE
            } else {
                binding.homeButton.visibility = View.GONE
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

    private fun setImage(placeBg: ImageView) {

        val ivBasicImage = activity?.findViewById(com.example.planmytrip20.R.id.place_bg) as ImageView
        Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg/1920px-La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg").into(ivBasicImage)

//        Picasso.get()
//            .load("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg/1920px-La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg")
//            .into(placeBg)
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