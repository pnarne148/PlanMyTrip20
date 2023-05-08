package com.example.planmytrip20.ui.itinerary.overview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.CardChosenPlacesBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import java.util.Collections


class ChosenPlacesAdapter(
    private val context: Context,
    private val viewModel: ItineraryViewModel,
    private val values: List<ItineraryLocation>
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

    fun onItemMoved(fromPosition: Int, toPosition: Int) {

        Log.d("itinerery", "onItemMoved: testing")

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(values, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(values, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onItemRemoved(position: Int) {
//        viewModel.unchoosePlace(position)
//        notifyItemRemoved(position)
    }

    inner class ViewHolder(private val binding: CardChosenPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: ItineraryLocation, position: Int) {
            binding.placeName.text = location.name
            binding.position.text = (position+1).toString()
            binding.placeDescription.text = location.description

            if(location.bitmap==null)
                binding.placeImage.setImageResource(R.drawable.placeholder_image)
            else
                binding.placeImage.setImageBitmap(location.bitmap)
        }
    }
}
