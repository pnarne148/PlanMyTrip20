package com.example.planmytrip20.ui.itinerary.tripDetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.ItineraryLocationListItemBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.example.planmytrip20.ui.itinerary.maps.MapBottomSheetFragment
import com.google.android.material.card.MaterialCardView
import com.google.gson.GsonBuilder


class ChecklistRecyclerViewAdapter(
    private val context: Context,
    private val viewModel: ItineraryViewModel,
    private val locations: List<ItineraryLocation>,
    private val lastVisited: Int,
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


        if(locations[position].visited && locations[position]?.user_photo_urls != null && locations[position]?.user_photo_urls?.isNotEmpty()!!) {
            holder.addPhotosLabelView.visibility = View.VISIBLE
            holder.photoListView.visibility = View.VISIBLE
        } else if(locations[position].visited && (locations[position]?.user_photo_urls == null || locations[position]?.user_photo_urls != null && !(locations[position].user_photo_urls?.isNotEmpty()!!))) {
            holder.photoListView.visibility = View.GONE
            holder.addPhotosLabelView.visibility = View.VISIBLE
        } else{
            holder.photoListView.visibility = View.GONE
            holder.addPhotosLabelView.visibility = View.GONE
        }


        Log.d(TAG, lastVisited.toString())

        if(position == lastVisited)
        {
            holder.placeElementsLayout.visibility = View.VISIBLE
            holder.checkBox.isEnabled = true
            holder.statusBar.setBackgroundColor(context.resources.getColor(R.color.green))
        } else if(position < lastVisited){
            holder.completedView.visibility = View.VISIBLE
            holder.statusBar.setBackgroundColor(context.resources.getColor(R.color.green))
            holder.verticalLineView.setBackgroundColor(context.resources.getColor(R.color.green))
        } else{
            Log.d(TAG, "onBindViewHolder: ")
            holder.placeElementsLayout.visibility = View.GONE
            holder.statusText.text = "Not Yet Started"
            holder.statusText.setTextColor(context.resources.getColor(R.color.red))
            holder.completedView.visibility = View.VISIBLE
            holder.statusText.visibility = View.VISIBLE
            holder.statusBar.setBackgroundColor(context.resources.getColor(R.color.darkgray))
            holder.verticalLineView.setBackgroundColor(context.resources.getColor(R.color.darkgray))
        }

        if(position == lastVisited+1)
            holder.checkBox.isEnabled = true

        holder.checkBox.setOnCheckedChangeListener { _, checked ->
            Log.d(TAG, "This is a OnCheckedChangeListener")
            if(checked)
            {
                viewModel.setIndex(lastVisited+1)
                viewModel.visitPlace(position,true)
            }
            else
            {
                viewModel.visitPlace(position,false)
                viewModel.setIndex(lastVisited-1)
            }
        }

        if(position != locations.size - 1){
            holder.carDirection.setOnClickListener{
                viewOnMap(locations[position], locations[position + 1], "DRIVING")
            }
            holder.bikeDirection.setOnClickListener{
                viewOnMap(locations[position], locations[position + 1], "BICYCLE")
            }
            holder.restaurantView.setOnClickListener{
                viewOnMap(locations[position], locations[position + 1], "restaurant")
            }
            holder.gasStationView.setOnClickListener{
                viewOnMap(locations[position], locations[position + 1], "gas_station")
            }
        }

        holder.addPhotosLabelView.setOnClickListener{
            //TODO: Add logic to add photos to each location and retrieve urls
        }

        holder.webViewLocation.setOnClickListener{
            holder.locDesc.maxLines = Integer.MAX_VALUE
            holder.hideDesc.visibility = View.VISIBLE
            holder.webViewLocation.visibility = View.GONE
        }

        holder.hideDesc.setOnClickListener{
//            openWebView?.invoke(locations[position].wikiUrl.toString())
            holder.locDesc.maxLines = 3
            holder.webViewLocation.visibility = View.VISIBLE
            holder.hideDesc.visibility = View.GONE
        }

    }

    private fun viewOnMap(loc1: ItineraryLocation, loc2: ItineraryLocation, mapType: String) {
        val modal = MapBottomSheetFragment()
        val bundle = Bundle()
        modal.dialog
        bundle.putString("source", GsonBuilder().create().toJson(loc1))
        bundle.putString("destination", GsonBuilder().create().toJson(loc2))
        bundle.putString("mapType", mapType)
        modal.arguments = bundle
        modal.show((context as AppCompatActivity).supportFragmentManager, "Map bottom sheet")
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
        var locDesc: TextView = binding.locationDescription
        var hideDesc: TextView = binding.hideDesc
    }

    companion object {
        const val TAG : String = "Checklist Adapter"
    }
}