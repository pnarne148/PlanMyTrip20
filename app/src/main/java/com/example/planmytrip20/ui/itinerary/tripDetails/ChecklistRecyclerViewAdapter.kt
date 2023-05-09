package com.example.planmytrip20.ui.itinerary.tripDetails

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.ItineraryLocationListItemBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.google.android.material.card.MaterialCardView

class ChecklistRecyclerViewAdapter(
    private val context: Context,
    private val viewModel: ItineraryViewModel,
    private val locations: List<ItineraryLocation>,
    private val lastVisited: Int
):
    RecyclerView.Adapter<ChecklistRecyclerViewAdapter.ModelViewHolder>()  {

    var openMap : ((ItineraryLocation, ItineraryLocation, String) -> Unit)? = null

    var onCheckBoxChange : ((Int, Boolean) -> Unit)? = null

    var openWebView : ((String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChecklistRecyclerViewAdapter.ModelViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItineraryLocationListItemBinding.inflate(inflater, parent, false)
        return ModelViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: ModelViewHolder,
        position: Int,
    ) {
        holder.locationName.text = locations[position].name
        holder.locationDesc.text = locations[position].description
        holder.ratingBar.numStars = locations[position].rating?.toInt() ?: 3

        Log.d("Location${position}", "${locations[position]}")

        holder.checkBox.isChecked = locations[position].visited

        if(position.equals(lastVisited))
        {
            Log.d(TAG, "onBindViewHolder: lastvisited"+lastVisited)
            Log.d(TAG, "onBindViewHolder: lastvisited"+position)
            holder.placeElementsLayout.visibility = View.VISIBLE
            holder.checkBox.isEnabled = true
        } else if(position < lastVisited){
            holder.completedView.visibility = View.VISIBLE
        } else{

        }

        if(position == lastVisited-1)
            holder.checkBox.isEnabled = true

        holder.checkBox.setOnCheckedChangeListener { button, checked ->
            Log.d("Adapter", "This is a OnCheckedChangeListener")
            if(checked)
            {
                viewModel.setIndex(lastVisited+1)
                viewModel.visitPlace(position,checked)
            }
            else
            {
                viewModel.visitPlace(position,checked)
                viewModel.setIndex(lastVisited-1)
            }
        }

        holder.addPhotosLabelView.setOnClickListener{
            //TODO: Add logic to add photos to each location and retrieve urls
        }

        holder.webViewLocation.setOnClickListener{
            openWebView?.invoke(locations[position].wikiUrl.toString())
        }

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
        var placeElementsLayout : LinearLayout = binding.placeElementsView
        var locationItemCardView : MaterialCardView = binding.locationItemCardView
        var checkBox : CheckBox = binding.locationVisited
        var completedView : LinearLayout = binding.completed
        var statusText: TextView = binding.statusText
        var statusBar : View = binding.statusBar
        var lineView : View = binding.lineView
        var verticalLineView : View = binding.verticalLine
        var carDirection: ImageView = binding.carDirectionsIcon
        var bikeDirection: ImageView = binding.bikeDirectionsIcon
        var restaurantView: ImageView = binding.restaurantsIcon
        var gasStationView: ImageView = binding.gasolineIcon
        var itineraryLayout: ConstraintLayout = binding.itineraryMainLayout
        var userOptionsView: LinearLayout = binding.userOptionsView
        var addPhotosLabelView: TextView = binding.addPhotos
        var photoListView: LinearLayout = binding.userImagesView
        var locationDescLayoutView : LinearLayout = binding.locationDescView
        var webViewLocation: TextView = binding.webViewLocation
    }

    companion object {
        const val TAG : String = "Checklist Adapter"
    }
}