package com.example.planmytrip20.ui.itinerary.overview

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.SelectedLocation
import com.example.planmytrip20.databinding.CardNotesItemBinding
import com.example.planmytrip20.databinding.CardPlacesItemBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class OverviewListAdapter(
    private val context: Context,
    private val viewModel: ItineraryViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val values: List<String>,
    private val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_NOTES = 1
        const val VIEW_TYPE_PLACES = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NOTES -> {
                val binding = CardNotesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderNotes(binding)
            }
            VIEW_TYPE_PLACES -> {
                val binding = CardPlacesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderPlaces(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]
        when (holder.itemViewType) {
            VIEW_TYPE_NOTES -> {
                val viewHolderNotes = holder as ViewHolderNotes
                viewHolderNotes.bind(item)
            }
            VIEW_TYPE_PLACES -> {
                val viewHolderPlaces = holder as ViewHolderPlaces
                viewHolderPlaces.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_NOTES
            1 -> VIEW_TYPE_PLACES
            else -> VIEW_TYPE_NOTES
        }
    }

    inner class ViewHolderNotes(private val binding: CardNotesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.cardType.text = item
            binding.notes.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.notes.setBackgroundResource(R.drawable.bg_rounded_rect)
                }
                else{
                    binding.notes.setBackgroundColor(Color.WHITE)
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            binding.done.setOnClickListener{
                binding.notes.clearFocus()
                viewModel.setNotes(binding.notes.text.toString())
            }
            binding.dropDown.setOnClickListener {

                if (binding.dropDown.drawable?.constantState?.equals(ContextCompat.getDrawable(context, R.drawable.ic_right_arrow)?.constantState) == true)
                {
                    binding.notes.visibility = View.VISIBLE
                    binding.dropDown.setImageResource(R.drawable.ic_down_arrow)
                }
                else
                {
                    binding.notes.visibility = View.GONE
                    binding.dropDown.setImageResource(R.drawable.ic_right_arrow)
                }
            }
        }
    }

    inner class ViewHolderPlaces(private val binding: CardPlacesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.cardType.text = item

            binding.selectedPlaces.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )

            handleAutoCompleteFrag()


            with(binding.selectedPlaces) {
                layoutManager = LinearLayoutManager(context)
                clipToPadding = false
                val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP,0){
                    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                        super.onSelectedChanged(viewHolder, actionState)
                        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                            viewHolder?.itemView?.elevation = 0f // or any other desired elevation
                        }else {
                            viewHolder?.itemView?.elevation = 0f // reset elevation
                        }
                    }

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {

                        val fromPosition = viewHolder.adapterPosition
                        val toPosition = target.adapterPosition

                        viewModel.swapChosenPlaces(fromPosition, toPosition)
                        notifyDataSetChanged()
                        adapter?.notifyItemMoved(fromPosition, toPosition)
                        adapter?.notifyItemChanged(toPosition)
                        adapter?.notifyItemChanged(fromPosition)

                        Log.d("itinerery", "onMove: testing")

                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        viewModel.unchoosePlace(viewHolder.adapterPosition)
                        notifyItemRemoved(viewHolder.adapterPosition)
                    }
                })

                itemTouchHelper.attachToRecyclerView(this)

                viewModel.chosenPlaces.observe(viewLifecycleOwner, Observer {
                    adapter = ChosenPlacesAdapter(context, viewModel, it)
                })
            }

            with(binding.recommendedPlaces) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                viewModel.recommendedPlaces.observe(viewLifecycleOwner, Observer {
                    adapter = RecommendedPlacesAdapter(context, viewModel, it)
                })
            }

            binding.dropDown.setOnClickListener {
                if (binding.dropDown.drawable?.constantState?.equals(ContextCompat.getDrawable(context, R.drawable.ic_right_arrow)?.constantState) == true)
                {
                    binding.placesLayout.visibility = View.VISIBLE
                    binding.dropDown.setImageResource(R.drawable.ic_down_arrow)
                }
                else
                {
                    binding.placesLayout.visibility = View.GONE
                    binding.dropDown.setImageResource(R.drawable.ic_right_arrow)
                }
            }

        }

        private fun handleAutoCompleteFrag() {

            viewModel.destination.observe(viewLifecycleOwner, Observer {
                val bounds = LatLngBounds(
                    LatLng(it.latLng?.latitude?.minus(10) ?: 37.7749, it.latLng?.longitude?.minus(10) ?: -122.4194),
                    LatLng(it.latLng?.latitude?.minus(10) ?: 37.7749, it.latLng?.longitude?.minus(10) ?: -122.4194),
                )

                val autocompleteFrag = AutocompleteSupportFragment()
                autocompleteFrag.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                val locationRestriction = RectangularBounds.newInstance(bounds)

                //code to bound location search
//                autocompleteFrag.setLocationRestriction(locationRestriction)

                val transaction = fragmentManager.beginTransaction()
                transaction.replace((R.id.addLocationFragment), autocompleteFrag)
                transaction.commit()

                autocompleteFrag.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                    override fun onPlaceSelected(place: Place) {
                        Log.d("itinerery", "onPlaceSelected: "+place.latLng)
                        viewModel.addPlace(SelectedLocation(place.name, place.latLng, emptyList(), emptyList()))
                    }

                    override fun onError(status: Status) {

                    }
                })

            })


        }
    }
}

