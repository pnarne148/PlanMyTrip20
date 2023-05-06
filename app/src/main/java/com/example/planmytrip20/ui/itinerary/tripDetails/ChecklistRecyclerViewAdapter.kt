package com.example.planmytrip20.ui.itinerary.tripDetails

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.ItineraryLocationListItemBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel

class ChecklistRecyclerViewAdapter(private val viewModel: ItineraryViewModel, private val locations: List<ItineraryLocation>):
    RecyclerView.Adapter<ChecklistRecyclerViewAdapter.ModelViewHolder>()  {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChecklistRecyclerViewAdapter.ModelViewHolder {
        context = parent.context

        return ModelViewHolder(
            ItineraryLocationListItemBinding.inflate(LayoutInflater.from(context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ModelViewHolder,
        position: Int,
    ) {
        holder.locationName.text = locations[position].name
        holder.locationDesc.text = locations[position].description
        holder.locationVisited.isChecked = false
        holder.ratingBar.numStars = locations[position].rating
    }

    override fun getItemCount(): Int {
        Log.d("ADapter", "getItemCount  = " + locations.size)
        return locations.size
    }

    inner class ModelViewHolder(binding: ItineraryLocationListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var locationName : TextView = binding.locationName
        var locationDesc : TextView = binding.locationDescription
        var locationVisited : CheckBox = binding.locationVisited
        var ratingBar : RatingBar = binding.ratingBar

    }
}