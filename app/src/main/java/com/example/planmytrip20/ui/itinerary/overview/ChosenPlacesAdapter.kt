package com.example.planmytrip20.ui.itinerary.overview

import android.R.attr.radius
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.RecommendedLocations
import com.example.planmytrip20.databinding.CardChosenPlacesBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


class ChosenPlacesAdapter(
    private val context: Context,
    private val viewModel: ItineraryViewModel,
    private val values: List<RecommendedLocations>
) : RecyclerView.Adapter<ChosenPlacesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardChosenPlacesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position], position)
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class ViewHolder(private val binding: CardChosenPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: RecommendedLocations, position: Int) {
            binding.placeName.text = location.name
            binding.position.text = (position+1).toString()
            binding.placeDescription.text = location.desc

            if(location.bitmap==null)
                binding.placeImage.setImageResource(R.drawable.placeholder_image)
            else
                binding.placeImage.setImageBitmap(location.bitmap)
        }
    }
}
