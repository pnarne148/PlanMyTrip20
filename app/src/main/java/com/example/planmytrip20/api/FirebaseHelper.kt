package com.example.planmytrip20.api

import android.util.Log
import com.example.planmytrip20.classes.ItineraryExport
import com.example.planmytrip20.classes.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FirebaseHelper() {

    val TAG = "Firebase"

    fun createNewItinerary(itinerary: ItineraryExport, callback: (String) -> Unit){
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("itinerary/"+FirebaseAuth.getInstance().currentUser?.uid+"/trips")

        var documentId: String? = null
        usersCollection.add(itinerary)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                callback(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun updateItinerary(documentId: String, updatedItinerary: ItineraryExport) {
        val db = FirebaseFirestore.getInstance()
        val itineraryRef = db.collection("itinerary/"+FirebaseAuth.getInstance().currentUser?.uid+"/trips").document(documentId)

        itineraryRef
            .update(
                "destination", updatedItinerary.destination,
                "attraction", updatedItinerary.attraction,
                "recommendations", updatedItinerary.recommendations
            )
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

    fun getEntireItinery(docReference: String){
        database.db.document("/itinerary/"+FirebaseAuth.getInstance().currentUser?.uid+"/trips/"+docReference)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // document exists
                    Log.d(TAG, "onCreate: "+document.data)
                    val itineraryExport = document.toObject(ItineraryExport::class.java)
                    if (itineraryExport != null) {
                        Log.d(TAG, "onCreate: "+itineraryExport.destination?.name)
                    }
                    // do something with itineraryExport
                } else {
                    // document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // error getting document
            }
    }

    fun getAllItineraries(callback: (List<ItineraryExport>) -> Unit){

        Log.d(TAG, "getAllItineraries: "+"/itinerary/"+FirebaseAuth.getInstance().currentUser?.uid+"/trips/")

        database.db.collection("/itinerary/"+FirebaseAuth.getInstance().currentUser?.uid+"/trips/")
            .get()
            .addOnSuccessListener { result ->
                var trips = emptyList<ItineraryExport>()
                for (document in result) {
                    val trip = document.toObject(ItineraryExport::class.java)
                    Log.d(TAG, "getAllItineraries: "+trip.destination?.name)

                    trips = trips.plus(trip)
                }
                Log.d(TAG, "getAllItineraries: "+trips.size)
                callback(trips)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                callback(emptyList())
            }
    }


}