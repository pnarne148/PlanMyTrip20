package com.example.planmytrip20.classes

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.Exclude

data class ItineraryLocation(
    var location_id: String, // id of location in our database
    var place_id: String?, // place id from google
    var name: String?,
    var address: String?,
    var openingHours: OpeningHours?,
    var latitude: Double,
    var longitude: Double,
    var visited : Boolean = false,
    var description: String?,
    val location_image_url: String? = null,
    var rating: Double?,
    var wikiUrl: String? = null,
    var user_photo_urls: List<String>? = emptyList(),
    @Exclude var bitmap: Bitmap? = null
): Parcelable {

    constructor() : this("", null, null, null, null, 0.0,0.0, false, null, null, null, null, null)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(OpeningHours::class.java.classLoader),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readParcelable(Bitmap::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(location_id)
        parcel.writeString(place_id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeParcelable(openingHours, flags)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeByte(if (visited) 1 else 0)
        parcel.writeString(description)
        parcel.writeString(location_image_url)
        parcel.writeValue(rating)
        parcel.writeString(wikiUrl)
        parcel.writeStringList(user_photo_urls)
        parcel.writeParcelable(bitmap, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    companion object CREATOR : Parcelable.Creator<ItineraryLocation> {
        override fun createFromParcel(parcel: Parcel): ItineraryLocation {
            return ItineraryLocation(parcel)
        }

        override fun newArray(size: Int): Array<ItineraryLocation?> {
            return arrayOfNulls(size)
        }
    }
}