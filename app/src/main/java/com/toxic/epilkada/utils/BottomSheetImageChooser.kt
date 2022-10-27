package com.toxic.epilkada.com.toxic.epilkada.utils

import com.toxic.epilkada.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.SurfaceRequest
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.toxic.epilkada.BuildConfig
import kotlinx.android.synthetic.main.activity_identifikasi_wajah.*
import kotlinx.android.synthetic.main.bottom_sheet_photo_fragment.*
import java.io.File

class BottomSheetImageChooser : BottomSheetDialogFragment() {

    private lateinit var getPhotosCallback: GetPhotosCallback
    private var listPhotoUri = ArrayList<Uri>()
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val clipData = it.data?.clipData
            listPhotoUri.clear()
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val item = clipData.getItemAt(i)
                    listPhotoUri.add(item.uri)
                }
            } else {
                val uri = it.data?.data
                if (uri != null) {
                    listPhotoUri.add(uri)
                }
            }
            getPhotosCallback.onGetPhotos(listPhotoUri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    var camFileUri : Uri? = null

    private val camResultLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (it && camFileUri!=null) {
            listPhotoUri.clear()
            listPhotoUri.add(camFileUri!!)
            getPhotosCallback.onGetPhotos(listPhotoUri)
        }
        /*if(it.resultCode == Activity.RESULT_OK && it.data!=null){
            it.data?.data?.let { uri->
                listPhotoUri.add(uri)
            }
        }*/
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_photo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_camera.setOnClickListener {
            //this.dismiss()
            /*val cameraFragment = CameraFragment()
            val manager: FragmentManager = requireActivity().supportFragmentManager
            manager.commit {
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.slide_out,
                    R.anim.slide_in,
                    R.anim.fade_out
                )
                replace(
                    R.id.containerFragment, cameraFragment
                )
                addToBackStack(cameraFragment.tag)
            }*/
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            camFileUri = initTempUri()
            camResultLauncher.launch(camFileUri)
        }
        iv_gallery.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            galleryIntent.action = Intent.ACTION_PICK
            resultLauncher.launch(galleryIntent)
        }
    }

    private fun initTempUri(): Uri {
        //gets the temp_images dir
        val directory = File(
            requireContext().filesDir, //this function gets the external cache dir
            "camera_images"
        ) //gets the directory for the temporary images dir

        if(!directory.exists()){
            directory.mkdir() //Create the temp_images dir
        }


        //Creates the temp_image.jpg file
        val file = File(
            directory, //prefix the new abstract path with the temporary images dir path
            "facetrain.jpg"
        ) //gets the abstract temp_image file name

        //Returns the Uri object to be used with ActivityResultLauncher
        return FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID+".provider",
            file
        )
    }

    fun setOnGetPhotosCallback(getPhotosCallback: GetPhotosCallback) {
        this.getPhotosCallback = getPhotosCallback
    }

    interface GetPhotosCallback {
        fun onGetPhotos(photoUris: List<Uri>)
    }

}