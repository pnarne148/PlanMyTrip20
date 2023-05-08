package com.example.planmytrip20.ui.itinerary.overview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planmytrip20.R
import com.example.planmytrip20.databinding.CardNotesItemBinding
import com.example.planmytrip20.databinding.CardPlacesItemBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel


class OverviewListAdapter(
    private val context: Context,
    private val viewModel: ItineraryViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val values: List<String>
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

            with(binding.selectedPlaces) {
                layoutManager = LinearLayoutManager(context)

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
    }
}

