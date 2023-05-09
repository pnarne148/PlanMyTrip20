package com.example.planmytrip20.ui.home.DetailFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.Item
import com.example.planmytrip20.ui.home.DetailFragment.DetailFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListFragment : Fragment(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: ListAdapter
    lateinit var itemList: List<Item>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from Firestore
        fetchDataFromFirestore()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// - Initial Approach - Now taking data from firebase
    //        itemList = generateShuffledItemList()
//        listAdapter = ListAdapter(itemList, this)
//        recyclerView.adapter = listAdapter
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize the list of items with image resources and descriptions
//
//    private fun generateShuffledItemList(): List<Item> {
//        return listOf(

        // Initialize the list of items with image resources and descriptions
//        itemList = listOf(
//
//            Item(R.drawable.australia, getString(R.string.australia), getString(R.string.australia_description)),
//            Item(R.drawable.canada, getString(R.string.canada), getString(R.string.canada_description)),
//            Item(R.drawable.china, getString(R.string.china), getString(R.string.china_description)),
//            Item(R.drawable.bali, getString(R.string.bali), getString(R.string.bali_description)),
//            Item(R.drawable.dubai, getString(R.string.dubai), getString(R.string.dubai_description)),
//            Item(R.drawable.florida, getString(R.string.florida), getString(R.string.florida_description)),
//            Item(R.drawable.france, getString(R.string.france), getString(R.string.france_description)),
//            Item(R.drawable.india, getString(R.string.india), getString(R.string.india_description)),
//            Item(R.drawable.italy, getString(R.string.italy), getString(R.string.italy_description)),
//            Item(R.drawable.russia, getString(R.string.russia), getString(R.string.russia_description)),
//            Item(R.drawable.singapore, getString(R.string.singapore), getString(R.string.singapore_description)),
//            Item(R.drawable.tokyo, getString(R.string.tokyo), getString(R.string.tokyo_description)),
//            Item(R.drawable.turkey, getString(R.string.turkey), getString(R.string.turkey_description)),
//            Item(R.drawable.brazil, getString(R.string.brazil), getString(R.string.brazil_description)),
//            Item(R.drawable.greece, getString(R.string.greece), getString(R.string.greece_description)),
//            Item(R.drawable.spain, getString(R.string.spain), getString(R.string.spain_description)),
//            Item(R.drawable.thailand, getString(R.string.thailand), getString(R.string.thailand_description)),
//            Item(R.drawable.new_zealand, getString(R.string.new_zealand), getString(R.string.new_zealand_description)),
//            Item(R.drawable.mexico, getString(R.string.mexico), getString(R.string.mexico_description)),
//            Item(R.drawable.south_africa, getString(R.string.south_africa), getString(R.string.south_africa_description)),
//            Item(R.drawable.ireland, getString(R.string.ireland), getString(R.string.ireland_description)),
//            Item(R.drawable.japan, getString(R.string.japan), getString(R.string.japan_description)),
//            Item(R.drawable.egypt, getString(R.string.egypt), getString(R.string.egypt_description)),
//            Item(R.drawable.germany, getString(R.string.germany), getString(R.string.germany_description)),
//            Item(R.drawable.argentina, getString(R.string.argentina), getString(R.string.argentina_description)),
//            Item(R.drawable.morocco, getString(R.string.morocco), getString(R.string.morocco_description)),
//            Item(R.drawable.switzerland, getString(R.string.switzerland), getString(R.string.switzerland_description)),
//            Item(R.drawable.united_arab_emirates, getString(R.string.united_arab_emirates), getString(R.string.united_arab_emirates_description)),
//            Item(R.drawable.peru, getString(R.string.peru), getString(R.string.peru_description))
//        ).shuffled()


//        // Create an instance of the ListAdapter and pass the item list and click listener
//        listAdapter = ListAdapter(itemList, this)
//
//        // Set the adapter on the RecyclerView
//        recyclerView.adapter = listAdapter
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
        listAdapter = ListAdapter(itemList, object : ListAdapter.OnItemClickListener {
            // Function called when an item in the list is clicked
            override fun onItemClick(position: Int) {
                // Get the destination details from the list of items at the clicked position
                val destination = itemList[position]

                // Create a new instance of the DetailFragment class
                val detailFragment = DetailFragment()

                // Pass the destination details to the DetailFragment constructor using arguments
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
                    .addToBackStack(null) // Add the replaced fragment to the back stack
                    .commit()
            }


        })
        recyclerView.adapter = listAdapter

    }


}
