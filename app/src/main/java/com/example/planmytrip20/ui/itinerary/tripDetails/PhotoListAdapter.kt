package com.example.planmytrip20.ui.itinerary.tripDetails

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load

import com.example.planmytrip20.R
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.databinding.ItineraryLocationListItemBinding
import com.example.planmytrip20.databinding.PhotoGalleryListItemBinding
import com.example.planmytrip20.ui.itinerary.ItineraryViewModel
import com.example.planmytrip20.ui.itinerary.maps.MapBottomSheetFragment
import com.google.android.material.card.MaterialCardView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.net.URI
import java.util.*

class PhotoListAdapter(
    private val context: Context,
    private val photoUrls: List<String>
):
    RecyclerView.Adapter<PhotoListAdapter.ModelViewHolder>()  {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PhotoListAdapter.ModelViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = PhotoGalleryListItemBinding.inflate(inflater, parent, false)
        return ModelViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: ModelViewHolder,
        position: Int,
    ) {
        holder.imageView.load(photoUrls[position])
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

        return photoUrls.size
    }

    inner class ModelViewHolder(binding: PhotoGalleryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var imageView : ImageView = binding.userImage
    }

    companion object {
        const val TAG : String = "Photo List Adapter"
    }

}