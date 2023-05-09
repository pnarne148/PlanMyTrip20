package com.example.planmytrip20

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.planmytrip20.api.DatabaseRequest
import com.example.planmytrip20.api.FirebaseHelper
import com.example.planmytrip20.classes.ItineraryExport
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.classes.OpeningHours
import com.example.planmytrip20.classes.database
import com.example.planmytrip20.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val TAG="MainActivity"
    private lateinit var placesClient: PlacesClient
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        GlobalScope.launch(Dispatchers.Main){
//            var response = async { DatabaseRequest.getQuery("users", "test", "nothing") }
//            Log.d(TAG, "Main Activity Response => ${response.await()}")
//        }

        var response = DatabaseRequest.getQuery("users", "test", "nothing")
        Log.d(TAG, "Main Activity Response => ${response.size}")

        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.maps_api_key), Locale.US);
            placesClient = Places.createClient(this)
        }

        // TODO : Just to test the firebase configuration. Should be removed
        database.db.collection("userDetails")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        var loc = ItineraryLocation("test_id", "test_place_id", "Brighton MA 7",
            "50 Winship Street",  OpeningHours(true), 42.9912, -80.456, false, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.",rating=2.0)

//        FirebaseHelper().createNewItinerary(ItineraryExport(loc, listOf(loc), listOf(loc, loc)))


    }

    companion object {}

    fun hideBottomNavigation() {
        navView.visibility = View.GONE
    }

    fun showBottomNavigation() {
        navView.visibility = View.VISIBLE
    }

    fun openHome() {
        navView.selectedItemId = R.id.navigation_home
        navView.visibility = View.VISIBLE
    }
}