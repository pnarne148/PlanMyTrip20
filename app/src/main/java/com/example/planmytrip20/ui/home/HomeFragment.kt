package com.example.planmytrip20.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planmytrip.api.PexelsApiService
import com.example.planmytrip.api.PexelsPhoto
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.Item
import com.example.planmytrip20.databinding.FragmentHomeBinding
import com.example.planmytrip20.ui.home.DetailFragment.DetailFragment
import com.example.planmytrip20.ui.home.DetailFragment.ListFragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerViewCard: RecyclerView
    private lateinit var listAdapter: HomepageAdapter
    private lateinit var itemList: List<Item>
    private lateinit var nativeAdView: NativeAdView
    private lateinit var adLoader: AdLoader.Builder
    private val userId: String? = Firebase.auth.currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerViewCard = binding.recyclerViewCard
        recyclerViewCard.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        MobileAds.initialize(requireContext()) {}


        return root
    }

    private fun fetchTopTravelPhotos() {
        // Create a list of different keywords
        val keywords = listOf(getString(R.string.travel),
            getString(R.string.nature),
            getString(R.string.landscapes),
            getString(R.string.cities),
            getString(R.string.adventure),
            getString(R.string.trekking),
            getString(R.string.monuments),
            getString(R.string.food)
        )

        // Shuffle the list to get a random keyword
        val randomKeyword = keywords.shuffled().first()

        lifecycleScope.launch {
            try {
                val response = PexelsApiService.retrofitService.searchPhotos(randomKeyword, 4, 1)
                displayTravelPhotos(response.photos)
            } catch (e: Exception) {
                Log.e("Homepage", "Error fetching travel photos: ", e)
            }
        }
    }
    
    
    private fun fetchDataFromFirestore() {
        val db = Firebase.firestore
        val itemList = mutableListOf<Item>()

        db.collection("recommendations")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = Item(document.data)
                    itemList.add(item)
                }
                // Set up RecyclerView with fetched data
                setupRecyclerView(itemList.shuffled())
            }
            .addOnFailureListener { exception ->
                Log.w("ListFragment", "Error getting documents: ", exception)
            }
    }

    private fun setupRecyclerView(itemList: List<Item>) {
        listAdapter = HomepageAdapter(itemList.take(12), object : HomepageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // handle click event
                // Get the destination details from the list of items
                val destination = itemList[position]

                // Create a new instance of the DetailFragment class
                val detailFragment = DetailFragment()

                // Pass the destination details to the DetailFragment constructor
                detailFragment.arguments = Bundle().apply {
                    putString("destinationName", destination.Name)
                }

                // Use the FragmentManager to replace the current fragment with the DetailFragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                    )
                    .replace(R.id.nav_host_fragment_activity_main, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }, ItemListOrientation.VERTICAL)
        recyclerViewCard.adapter = listAdapter
    }

    private fun displayTravelPhotos(photos: List<PexelsPhoto>) {
        // Use a GridLayout with 2 columns to display the photos
        val gridLayout = view?.findViewById<GridLayout>(R.id.travelPhotosGridLayout)
        gridLayout?.columnCount = 1
        val inflater = LayoutInflater.from(requireContext())

        photos.forEach { photo ->
            // Inflate the travel_photo_item layout
            val travelPhotoItemView = inflater.inflate(R.layout.travel_photo_item, gridLayout, false)

            // Find and set the ImageView, Photographer's Name, and Photo Source in the layout
            val imageView = travelPhotoItemView.findViewById<ImageView>(R.id.travelPhotoImageView)
            val photographerNameTextView = travelPhotoItemView.findViewById<TextView>(R.id.photographerNameTextView)
            val photoSourceTextView = travelPhotoItemView.findViewById<TextView>(R.id.photoSourceTextView)

            // Load the photo URL into the ImageView using Glide
            Glide.with(requireContext())
                .load(photo.src.large)
                .into(imageView)

            // Set the photographer's name
            val photographerNameString = getString(R.string.pexelPhotoBy)
            photographerNameTextView.text = photographerNameString + ": " +photo.photographer

            // Set the photo source (assuming Pexels as the source)
            photoSourceTextView.text = getString(R.string.pexelSource)

            // Add the travel_photo_item layout to the GridLayout
            gridLayout?.addView(travelPhotoItemView)
        }
    }

    private fun fetchUsername(userId: String?) {
        if (userId == null) {
            Log.w("Homepage", "User ID is null")
            return
        }

        val db = Firebase.firestore
        db.collection("userDetails")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("firebase", "fetchUsername: ")
                    val username = document.getString("userName") ?: "User"
                    updateWelcomeMessage(username)
                } else {
                    Log.w("Homepage", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Homepage", "Error getting username: ", exception)
            }
    }

    private fun updateWelcomeMessage(username: String) {
        binding.welcomeTextView.text = "Welcome $username!"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        /**  code for welcome username starts */

        fetchUsername(userId)

//        /**  code for welcome username ends */

        // Fetch data from Firestore
        fetchDataFromFirestore()

        // Pexels api data retrieval
        fetchTopTravelPhotos()

        // Find the EditText
        val editText = binding.editTextSearchHomepage

        val goButton = binding.buttonGoHomepage
        goButton.setOnClickListener {
            // Handle button click event here
            val destination = editText.text.toString()


            // Create a new instance of the DetailFragment class
            val detailFragment = DetailFragment()

            // Pass the destination details to the DetailFragment constructor
            detailFragment.arguments = Bundle().apply {
                putString("destinationName", destination)
            }

            // Use the FragmentManager to replace the current fragment with the DetailFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
                )
                .replace(R.id.nav_host_fragment_activity_main, detailFragment)
                .addToBackStack(null)
                .commit()

        }


        binding.viewAllButton.setOnClickListener {
            // Replace the current fragment with the ListFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
                )
                .replace(R.id.nav_host_fragment_activity_main, ListFragment())
                .addToBackStack(null)
                .commit()
        }


        /** Banner ad code */


        // Find the AdView in the layout
        val adView = binding.adView

        // Create an AdRequest and load the banner ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Optional: Set an AdListener to listen for ad loading and error events
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("Homepage", "Banner ad loaded successfully")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("Homepage", "Banner ad failed to load: ${loadAdError.message}")
            }
        }

        /** Native ad code */

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}