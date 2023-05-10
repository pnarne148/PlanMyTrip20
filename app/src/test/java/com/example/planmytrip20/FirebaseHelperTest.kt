package com.example.planmytrip20

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.planmytrip20.api.FirebaseHelper
import com.example.planmytrip20.classes.Itinerary
import com.example.planmytrip20.classes.ItineraryExport
import com.example.planmytrip20.classes.ItineraryLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseHelperTest {

    private lateinit var firebaseAuthMock: FirebaseAuth
    private lateinit var firebaseFirestoreMock: FirebaseFirestore
    private lateinit var firebaseHelper: FirebaseHelper
    var TAG = "testing"

    @Before
    fun setup() {
        firebaseAuthMock = mockk(relaxed = true)
        firebaseFirestoreMock = mockk(relaxed = true)
        firebaseHelper = FirebaseHelper(firebaseAuthMock, firebaseFirestoreMock)
    }

    @Test
    fun createNewItinerary_success() {
        val iti = ItineraryLocation("test", "test", "test", "test", null, 0.0, 0.0, false, "test", "test", 0.0,"test")
        val itinerary = ItineraryExport(iti, listOf(iti, iti), listOf(iti), "test", "test")
        val documentReferenceMock = mockk<Task<DocumentReference>>()
        every { firebaseFirestoreMock.collection("itinerary/${firebaseAuthMock.currentUser?.uid}/trips") } returns mockk()
        every { firebaseFirestoreMock.collection("itinerary/${firebaseAuthMock.currentUser?.uid}/trips").add(itinerary) } returns documentReferenceMock

        Log.d(TAG, "createNewItinerary_success: ")
        firebaseHelper.createNewItinerary(itinerary) { documentId ->
            assertNotEquals("", documentId)
            assertNotEquals(null, documentId)
        }
        verify { firebaseFirestoreMock.collection("itinerary/${firebaseAuthMock.currentUser?.uid}/trips").add(itinerary) }
    }

//    @Test
//    fun updateItinerary_success() {
//        val iti = ItineraryLocation("test", "test", "test", "test", null, 0.0, 0.0, false, "test", "test", 0.0,"test")
//        val itinerary = ItineraryExport(iti, listOf(iti, iti), listOf(iti), "test", "test")
//        val itineraryRefMock = mockk<FirebaseFirestore>().mockk()
//        every { firebaseFirestoreMock.getInstance() } returns itineraryRefMock
//
//        // Act
//        firebaseHelper.updateItinerary(documentId, updatedItinerary)
//
//        // Assert
//        verify { itineraryRefMock.update("destination", updatedItinerary.destination,
//            "attraction", updatedItinerary.attraction,
//            "recommendations", updatedItinerary.recommendations) }
//    }

}
