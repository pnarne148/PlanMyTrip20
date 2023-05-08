package com.example.planmytrip20.ui.profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.planmytrip20.databinding.ActivityEditProfileBinding
import com.example.planmytrip20.databinding.ActivityLoginBinding
import com.example.planmytrip20.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraCheckPermission()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                displayImageUrl(photoUrl.toString())
            }
        }

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let { data ->
                        val bitmap = data.extras?.get("data") as Bitmap
                        val uri = bitmap.toUri(this)
                        uploadImageToFirebaseStorage(uri)
                        //we are using coroutine image loader (coil)
                    }
                }
            }

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let { data ->
                        data.data?.let { uri ->
                            uploadImageToFirebaseStorage(uri)
                        }
                    }
                }
            }


        //when you click on the image
        binding.profilePhoto.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf(
                "Select photo from Gallery",
                "Capture photo from Camera"
            )
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->
                when (which) {
                    0 -> galleryLauncher.launch(
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                    )
                    1 -> cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                }
            }
            pictureDialog.show()
        }
    }

    private fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(object :
                MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            camera()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRotationalDialogForPermission()
                }

            }
            ).onSameThread().check()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun galleryCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    applicationContext,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: com.karumi.dexter.listener.PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(applicationContext)
            .setMessage(
                "It looks like you have turned off permissions"
                        + "required for this feature. It can be enable under App settings!!!"
            )

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", applicationContext.packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun uploadImageToFirebaseStorage(uri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$fileName")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Display the image URL
                    displayImageUrl(downloadUrl.toString())

                    // Set the user photo URL in Firebase Authentication
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(ContentValues.TAG, "User photo URL updated.")
                                } else {
                                    Log.e(
                                        ContentValues.TAG,
                                        "Error updating user photo URL.",
                                        task.exception
                                    )
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    applicationContext,
                    "Failed to upload image: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun displayImageUrl(url: String) {
        // Display the image URL in a TextView or any other desired way
        binding.profilePhoto.load(url) {
            crossfade(true)
            crossfade(1000)
            transformations(CircleCropTransformation())
        }
    }

    fun Bitmap.toUri(context: Context): Uri {
        val bytes = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, this, "tempImage", null)
        return Uri.parse(path)
    }
}