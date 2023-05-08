package com.example.planmytrip20.ui.itinerary.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.CardRecommendedPlacesBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel


public class RecommendedPlacesAdapter(
    private val context: Context,
    private val viewModel: ItineraryViewModel,
    private val values: List<ItineraryLocation>
) : RecyclerView.Adapter<RecommendedPlacesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardRecommendedPlacesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class ViewHolder(private val binding: CardRecommendedPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: ItineraryLocation) {
            binding.placeName.text = location.name

            if(location.bitmap==null)
                binding.placeImage.setImageResource(R.drawable.placeholder_image)
            else
            {
                binding.placeImage.setImageBitmap(location.bitmap)
            }

            binding.addPlace.setOnClickListener{
                viewModel.choosePlace(location)
            }
        }
    }
}
