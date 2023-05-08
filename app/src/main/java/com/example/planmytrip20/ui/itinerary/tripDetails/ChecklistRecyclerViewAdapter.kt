package com.example.planmytrip20.ui.itinerary.tripDetails

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import com.example.planmytrip20.MainActivity
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.ItineraryLocationListItemBinding
import com.example.planmytrip20.ui.common.WebViewFragment
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.example.planmytrip20.ui.itinerary.maps.MapBottomSheetFragment
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.NonDisposableHandle.parent

class ChecklistRecyclerViewAdapter(private val viewModel: ItineraryViewModel, private val locations: List<ItineraryLocation>):
    RecyclerView.Adapter<ChecklistRecyclerViewAdapter.ModelViewHolder>()  {

    private lateinit var context: Context

    var openMap : ((ItineraryLocation, ItineraryLocation, String) -> Unit)? = null

    var onCheckBoxChange : ((Int, Boolean) -> Unit)? = null

    var openWebView : ((String) -> Unit)? = null

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
        holder.ratingBar.numStars = locations[position].rating?.toInt() ?: 3

        Log.d("Location${position}", "${locations[position]}")

        holder.checkBox.isChecked = locations[position].visited

        if(locations[position].visited && locations[position]?.user_photo_urls != null && locations[position]?.user_photo_urls?.isNotEmpty()!!) {
            holder.locationDescLayoutView.removeView(holder.photoListView)
            holder.userOptionsView.removeView(holder.addPhotosLabelView)
            holder.userOptionsView.addView(holder.addPhotosLabelView)
            holder.locationDescLayoutView.addView(holder.photoListView)
        } else if(locations[position].visited && (locations[position]?.user_photo_urls == null || locations[position]?.user_photo_urls != null && !(locations[position].user_photo_urls?.isNotEmpty()!!))) {
            holder.locationDescLayoutView.removeView(holder.photoListView)
            holder.userOptionsView.removeView(holder.addPhotosLabelView)
            holder.userOptionsView.addView(holder.addPhotosLabelView)
        } else{
            holder.locationDescLayoutView.removeView(holder.photoListView)
            holder.userOptionsView.removeView(holder.addPhotosLabelView)
        }

        if(locations[position].visited && position+1 < locations.size && locations[position+1].visited){
            holder.itineraryLayout.removeView(holder.placeElementsLayout)
            holder.itineraryLayout.removeView(holder.completedView)
            holder.itineraryLayout.addView(holder.completedView)
            holder.statusText.text = "Completed"
            holder.statusText.setTextColor(context.resources.getColor(R.color.green))
            holder.statusBar.setBackgroundColor(context.resources.getColor(R.color.green))
            holder.lineView.setBackgroundColor(context.resources.getColor(R.color.green))
            holder.verticalLineView.setBackgroundColor(context.resources.getColor(R.color.green))

            holder.checkBox.isEnabled = false
        }

        else if(locations[position].visited && position+1 < locations.size && !locations[position+1].visited){
            holder.itineraryLayout.removeView(holder.completedView)
            holder.itineraryLayout.removeView(holder.placeElementsLayout)
            holder.itineraryLayout.addView(holder.placeElementsLayout)
            holder.lineView.setBackgroundColor(context.resources.getColor(R.color.yellow))

            holder.checkBox.isEnabled = true
        }

        else if(!locations[position].visited && position+1 < locations.size && !locations[position+1].visited){
            holder.itineraryLayout.removeView(holder.placeElementsLayout)
            holder.itineraryLayout.removeView(holder.completedView)
            holder.itineraryLayout.addView(holder.completedView)
            holder.statusText.text = "Not Yet Started"
            holder.statusText.setTextColor(Color.RED)
            holder.statusBar.setBackgroundColor(context.resources.getColor(R.color.lightgray))
            holder.lineView.setBackgroundColor(context.resources.getColor(R.color.lightgray))
            holder.verticalLineView.setBackgroundColor(context.resources.getColor(R.color.lightgray))
        }
        if(position == 0 && !locations[position].visited){
            holder.checkBox.isEnabled = true
        }else if(position-1 > 0 && !locations[position-1].visited && !locations[position].visited && position+1 < locations.size && !locations[position+1].visited){
            holder.checkBox.isEnabled = false
        } else if(position-1 > 0 && locations[position-1].visited && !locations[position].visited && position+1 < locations.size && !locations[position+1].visited){
            holder.checkBox.isEnabled = true
        } else if(position == locations.size - 1 && !locations[position].visited && position-1 > 0 && locations[position-1].visited){
            holder.checkBox.isEnabled = true
        }

        if(position == locations.size - 1 && locations[position].visited) {
            holder.placeElementsLayout.removeAllViews()
            holder.completedView.removeAllViews()
            holder.statusBar.setBackgroundColor(context.resources.getColor(R.color.green))
        }

        if(position == locations.size - 1 && !locations[position].visited) {
            holder.placeElementsLayout.removeAllViews()
            holder.completedView.removeAllViews()
            holder.statusBar.setBackgroundColor(context.resources.getColor(R.color.lightgray))
        }

        Log.d("position", "$position")
        Log.d("Locations Size", "${locations.size - 1}")
        Log.d("Condition Check", "${position == locations.size - 1}")
        if(position != locations.size - 1){
            holder.carDirection.setOnClickListener{
                openMap?.invoke(locations[position], locations[position + 1], "DRIVING")
            }
            holder.bikeDirection.setOnClickListener{
                openMap?.invoke(locations[position], locations[position + 1], "BICYCLE")
            }

            holder.restaurantView.setOnClickListener{
                openMap?.invoke(locations[position], locations[position + 1], "restaurant")
            }
            holder.gasStationView.setOnClickListener{
                openMap?.invoke(locations[position], locations[position + 1], "gas_station")
            }
        }

        holder.checkBox.setOnCheckedChangeListener { button, checked ->
            Log.d("Adapter", "This is a OnCheckedChangeListener")
            onCheckBoxChange?.invoke(position, checked)
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