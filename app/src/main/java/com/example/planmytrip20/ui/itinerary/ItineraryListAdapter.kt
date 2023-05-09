package com.example.planmytrip20.ui.itinerary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.classes.Itinerary
import com.example.planmytrip20.databinding.ItineraryListItemBinding

class ItineraryListAdapter (private val context: Context,
                            private val viewModel: ItineraryViewModel,
                            private val itineraries: List<Itinerary>): RecyclerView.Adapter<ItineraryListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItineraryListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItineraryListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItineraryListAdapter.ViewHolder, position: Int) {
        holder.locationName.text = itineraries[position].name
        holder.latLngLocation.text = "${itineraries[position].latLng.latitude}, ${itineraries[position].latLng.longitude}"

        //TODO: update the image view src with itinerary image url

        holder.deleteButton.setOnClickListener{
            //TODO: call delete function to delete the item from the list and update in the database
        }
    }

    override fun getItemCount(): Int {
        return itineraries.size
    }

    class ViewHolder(binding: ItineraryListItemBinding)  : RecyclerView.ViewHolder(binding.root){
        var itineraryImageView: ImageView = binding.itineraryItemImageView
        var locationName: TextView = binding.itineraryItemHeaderView
        var latLngLocation: TextView = binding.latLngLocation
        var deleteButton: ImageView = binding.deleteItineraryIcon
    }

    companion object {
        const val TAG : String = "Itinerary List Adapter"
    }

}