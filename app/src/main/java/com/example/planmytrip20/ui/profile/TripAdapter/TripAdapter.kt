package com.example.planmytrip20.ui.profile.TripAdapter

import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.request.RequestOptions
import com.example.planmytrip20.R
import com.example.planmytrip20.api.FirebaseHelper
import com.example.planmytrip20.classes.ItineraryExport
import com.example.planmytrip20.databinding.ItineraryListItemBinding
import com.example.planmytrip20.ui.itinerary.ItineraryFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TripAdapter (private var trips: List<ItineraryExport>,
                   private var documentIDs: List<String>) :
    RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    // Define ViewHolder class here

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate layout for ViewHolder here
        val binding = ItineraryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to ViewHolder here
        holder.bind(trips[position], documentIDs[position], this)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    fun removeItem(position: Int) {
        trips = trips.toMutableList().apply {
            removeAt(position)
        }
        documentIDs = documentIDs.toMutableList().apply {
            removeAt(position)
        }
        notifyItemRemoved(position)
    }

    class ViewHolder(private val binding: ItineraryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: ItineraryExport, document: String, adapter: TripAdapter) {
            binding.itineraryItemHeaderView.text = "Trip to "+trip.destination?.name

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = LocalDateTime.parse(trip.date, formatter)

            binding.dateItem.text = getTimeAgoString(dateTime)

            Log.d("firebase", "bind: "+trip.destination?.location_image_url)

            val requestOptions = RequestOptions().apply {
                sizeMultiplier(0.5f) // Resize the image to half its original size
                placeholder(R.drawable.placeholder_image) // Show a placeholder while the image is loading
            }

            binding.itineraryItemImageView.load(trip.destination?.location_image_url) {
                crossfade(true)
                crossfade(1000)
            }

            binding.deleteItineraryIcon.setOnClickListener {
                FirebaseHelper().deleteItinerary(document)
                adapter.removeItem(adapterPosition)
            }

            binding.itineraryDesc.setOnClickListener{
                val fragment = ItineraryFragment()

                val bundle = Bundle()
                bundle.putString("document", document)
                fragment.arguments = bundle


//                val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
//                transaction.addToBackStack(null)
//                transaction.setReorderingAllowed(true)
//                transaction.commit()
            }
        }

        fun getTimeAgoString(time: LocalDateTime): String {
            val now = LocalDateTime.now()

            val days = ChronoUnit.DAYS.between(time, now)
            if (days > 0) {
                return if (days == 1L) "1 day ago" else "$days days ago"
            }

            val hours = ChronoUnit.HOURS.between(time, now)
            if (hours > 0) {
                return if (hours == 1L) "1 hour ago" else "$hours hours ago"
            }

            val minutes = ChronoUnit.MINUTES.between(time, now)
            return if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
        }
    }
}