package com.example.planmytrip20.ui.profile.TripAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.request.RequestOptions
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryExport
import com.example.planmytrip20.databinding.ItineraryListItemBinding
import java.time.format.DateTimeFormatter

class TripAdapter (private val trips: List<ItineraryExport>) :
    RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    // Define ViewHolder class here

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate layout for ViewHolder here
        val binding = ItineraryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to ViewHolder here
        holder.bind(trips[position])
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    class ViewHolder(private val binding: ItineraryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: ItineraryExport) {
            binding.itineraryItemHeaderView.text = "Trip to "+trip.destination?.name

            val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
            binding.dateItem.text = trip.date?.format(formatter)

            Log.d("firebase", "bind: "+trip.destination?.location_image_url)

            val requestOptions = RequestOptions().apply {
                sizeMultiplier(0.5f) // Resize the image to half its original size
                placeholder(R.drawable.placeholder_image) // Show a placeholder while the image is loading
            }

            binding.itineraryItemImageView.load(trip.destination?.location_image_url) {
                crossfade(true)
                crossfade(1000)
            }
        }
    }
}