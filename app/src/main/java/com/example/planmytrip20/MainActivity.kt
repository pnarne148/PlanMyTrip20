package com.example.planmytrip20

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.planmytrip20.api.FirebaseHelper
import com.example.planmytrip20.classes.ItineraryExport
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.classes.OpeningHours
import com.example.planmytrip20.classes.database
import com.example.planmytrip20.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.LatLng
import com.example.planmytrip20.ui.profile.OnBackPressedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

//        var response = DatabaseRequest.getQuery("users", "test", "nothing")
//        Log.d(TAG, "Main Activity Response => ${response.size}")

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

        binding.sosButton.setOnClickListener {
            sendLocationBySms()
        }

        fetchSOSstatus {
            Log.d("firebase", "onCreate: "+it)
            if(it.equals("false") or it.equals(null))
                binding.sosButton.visibility = View.GONE
            else
                binding.sosButton.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        if (currentFragment is OnBackPressedListener && currentFragment.onBackPressed()) {
            // The back press event has been consumed by the fragment
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val CALL_PHONE_REQUEST_CODE = 1001
        private const val SEND_SMS_REQUEST_CODE = 1002
        private const val ACCESS_FINE_LOCATION_REQUEST_CODE = 1003
    }

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

    private fun callEmergencyNumber(emergencyContact: String?) {
        if (emergencyContact == null) {
            return
        }
        if (hasCallPhonePermission()) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$emergencyContact")
            startActivity(intent)
        } else {
            requestCallPhonePermission()
        }
    }

    fun enableSOS(){
        binding.sosButton.visibility = View.VISIBLE
    }

    fun disableSOS(){
        binding.sosButton.visibility = View.GONE
    }


    @SuppressLint("MissingPermission")
    private fun sendLocationBySms() {
        if (!hasSendSmsPermission()) {
            requestSendSmsPermission()
        } else if (!hasAccessFineLocationPermission()) {
            requestAccessFineLocationPermission()
        } else {
            fetchEmergencyContact { emergencyContact ->
                if (emergencyContact == null) {
                    return@fetchEmergencyContact
                }
                callEmergencyNumber(emergencyContact)

                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        val googleMapsUrl = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                        val message = "Help!! My current location is: $googleMapsUrl"
                        SmsManager.getDefault().sendTextMessage(emergencyContact, null, message, null, null)
                        Toast.makeText(this@MainActivity, "SOS message sent with location.", Toast.LENGTH_SHORT).show()
                        locationManager.removeUpdates(this)
                    }

                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
            }
        }
    }



    private fun fetchEmergencyContact(callback: (String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            callback(null)
            return
        }

        firestore.collection("userDetails").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val emergencyContact = document.getString("emergencyContact")
                    callback(emergencyContact)
                } else {
                    Toast.makeText(this, "No emergency contact found", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching emergency contact: $exception", Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }

    private fun fetchSOSstatus(callback: (String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            callback(null)
            return
        }

        firestore.collection("userDetails").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("firebase", "fetchSOSstatus: "+document)
                    val emergencyContact = document.getString("sos")
                    callback(emergencyContact)
                } else {
                    Toast.makeText(this, "No emergency contact found", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching emergency contact: $exception", Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }


    private fun hasCallPhonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCallPhonePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_REQUEST_CODE)
    }

    private fun hasSendSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSendSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_REQUEST_CODE)
    }

    private fun hasAccessFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAccessFineLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PHONE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchEmergencyContact { emergencyContact ->
                        callEmergencyNumber(emergencyContact)
                    }
                } else {
                    Toast.makeText(this, "Permission denied, unable to call emergency number.", Toast.LENGTH_SHORT).show()
                }
            }
            SEND_SMS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendLocationBySms()
                } else {
                    Toast.makeText(this, "Permission denied, unable to send SMS with location.", Toast.LENGTH_SHORT).show()
                }
            }
            ACCESS_FINE_LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendLocationBySms()
                } else {
                    Toast.makeText(this, "Permission denied, unable to get location.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




}