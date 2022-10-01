package com.toxic.epilkada

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.hardware.Camera
import android.os.Build
import android.transition.Scene
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import java.lang.Exception

class ShowCamera(context: Context?, camera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

    private val holder1: SurfaceHolder = holder
    private var cam = camera

    init {
        cam = camera
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }


    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        cam.stopPreview()
        cam.release()
        Log.d("CameraDestroy","Camera stopped")
        //cam.release()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun surfaceCreated(p0: SurfaceHolder?) {
        val params = cam.parameters
        val sizes = params.supportedPreviewSizes
        var mSize: Camera.Size? = null
        val previewSizeOptimal: Camera.Size? =
            getOptimalPreviewSize(sizes, params.pictureSize.width, params.pictureSize.height)
        if(cam.parameters.focusMode=="fixed"){
            if (this.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait")
                cam.setDisplayOrientation(90)
                params.setRotation(90)
            } else {
                params.set("orientation", "landscape")
                cam.setDisplayOrientation(0)
                params.setRotation(0)
            }
            if (previewSizeOptimal != null) {
                params.setPreviewSize(previewSizeOptimal.width, previewSizeOptimal.height)
            }
        }
        else {

            if (previewSizeOptimal != null) {
                params.setPreviewSize(previewSizeOptimal.width, previewSizeOptimal.height)
            }
            if (this.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait")
                cam.setDisplayOrientation(90)
                params.setRotation(90)
            } else {
                params.set("orientation", "landscape")
                cam.setDisplayOrientation(0)
                params.setRotation(0)
            }

            for (a in sizes) {
                mSize = a
            }
            //params.setPictureSize(params.previewSize.width,params.previewSize.height)
            Log.d("Kualias", params.jpegQuality.toString())
            Log.d(
                "Kualitas",
                "Format Gambar = " + cam.parameters.pictureFormat + " Format preview = " + cam.parameters.previewFormat
            )
            params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            params.pictureFormat = ImageFormat.NV21
        }
        //params.setPictureSize(mSize!!.width,mSize.height)
        cam.parameters = params
        try {
            cam.setPreviewDisplay(holder)
            cam.startPreview()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getOptimalPreviewSize(
        sizes: List<Camera.Size>,
        width: Int,
        height: Int
    ): Camera.Size? {
        var ASPECT_TOLERANCE = 0.1
        var targetRatio = width.toDouble() / height
        if (sizes == null) {
            return null
        }
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE
        var targetHeight = height

        for (a in sizes) {
            var ratio = a.width.toDouble() / a.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue
            }
            if (Math.abs(a.height - targetHeight) < minDiff) {
                optimalSize = a
                minDiff = Math.abs(a.height - targetHeight).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (a in sizes) {
                if (Math.abs(a.height - targetHeight) < minDiff) {
                    optimalSize = a
                    minDiff = Math.abs(a.height - targetHeight).toDouble()
                }
            }
        }
        return optimalSize
    }

}
