package com.toxic.epilkada

import android.app.AlertDialog
import android.app.ProgressDialog
import android.app.ProgressDialog.show
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
/*import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText*/
import kotlinx.android.synthetic.main.activity_identifikasi.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class Identifikasi : AppCompatActivity() {
    //New line added
    private lateinit var cameraExecutor : ExecutorService
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private var imageCapture: ImageCapture? = null
    private var image : InputImage? = null
    //=================
    private lateinit var mcurrentPhotoPath: String
    private var mat: Mat? = null
    private var camera: Camera? = null
    lateinit var db: FirebaseFirestore
    lateinit var gg: Bitmap
    var dataExtra: pemilih? = null
    var dialog: ProgressDialog? = null
    final val PERMISSION_ALL: Int = 1
    var PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    var fileS: File? = null

    //Loader inisialisasi library OpenCV
    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i("OpenCV", "OpenCV loaded successfully")
                    mat = Mat()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identifikasi)
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        OpenCVLoader.initDebug()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera(){
        val cameraProviderFuture=ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable{
            val cameraProvider :ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(frame_camera.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(Size(720,1280))
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try{
                cameraProvider.unbindAll()
                val cam = cameraProvider.bindToLifecycle(
                    this,cameraSelector,preview,imageCapture
                )
                frame_camera.setOnTouchListener { view, motionEvent ->
                    val meteringPoint = frame_camera.meteringPointFactory
                        .createPoint(motionEvent.x,motionEvent.y)
                    val action = FocusMeteringAction.Builder(meteringPoint)
                        .build()
                    val result = cam.cameraControl.startFocusAndMetering(action)
                    result.addListener({
                        //Log.d("Identifikasi KTP",result.get().isFocusSuccessful.toString())
                    },ContextCompat.getMainExecutor(this@Identifikasi))
                    true
                }
            }catch (exc:Exception){
                Log.e("Identifikasi KTP","Use case binding failed",exc)
            }
        }, ContextCompat.getMainExecutor(this@Identifikasi))
    }

    fun takePicture(){
        imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("Identifikasi KTP", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    output.savedUri?.let {
                        image = InputImage.fromFilePath(this@Identifikasi,
                            it
                        )
                        textRecognize()
                    }
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("Identifikasi KTP", msg)
                }
            }
        )
    }

    fun textRecognize(){
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        image?.let { mImage->
            /*val bmp =mImage.bitmapInternal
            val testBmp = mImage.bitmapInternal
            val bitmapGray1 =
                Mat(mImage.width, mImage.height, CvType.CV_8UC1)
            Utils.bitmapToMat(testBmp, bitmapGray1)
            Imgproc.cvtColor(bitmapGray1, bitmapGray1, Imgproc.COLOR_BGR2GRAY)
            Utils.matToBitmap(bitmapGray1, testBmp)
            ivGrayscaled.setImageBitmap(testBmp)*/

            val koefX = mImage.width.toFloat() / frame_camera.width
            val koefY = mImage.height.toFloat() / frame_camera.height

            val x1 = view_border.left
            val y1 = view_border.top
            val x2 = view_border.width
            val y2 = view_border.height
            val cropStartX = Math.round(x1 * koefX)
            val cropStartY = Math.round(y1 * koefY)
            val cropWidthX = Math.round(x2 * koefX)
            val cropHeightY = Math.round(y2 * koefY)
            var croppedBitmap: Bitmap? = null
            if (cropStartX + cropWidthX <= mImage.width && cropStartY
                + cropHeightY <= mImage.height
            ) {
                croppedBitmap = Bitmap.createBitmap(
                    mImage.bitmapInternal!!,
                    cropStartX,
                    cropStartY,
                    cropWidthX,
                    cropHeightY
                )

            }
            //ivblackWhite.setImageBitmap(croppedBitmap)
            val inputImage = InputImage.fromBitmap(croppedBitmap!!,0)
            recognizer.process(inputImage)
                .addOnSuccessListener {
                    formatText(it)
                    compareText()
                    Log.d("Recognized Text","Success")
                }
        }
    }

    fun compareText(){
        if (hasil.length == 6 || hasil.length == 16 || hasil.length == 15 || hasil.length == 14 || hasil.length == 17) {
            val docRef = db.collection("users").document(hasil)
            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val temp = documentSnapshot.data!!
                        Toast.makeText(
                            this@Identifikasi,
                            "Proses pengenalan selesai",
                            Toast.LENGTH_LONG
                        ).show()
                        val tempFoto: ArrayList<String> = ArrayList()
                        for (a in (temp["foto"] as ArrayList<String>)) {
                            tempFoto.add(a)
                            Log.d("Pesan", a)
                        }
                        dataExtra = pemilih(
                            hasil,
                            temp["nama"].toString(),
                            temp["agama"].toString(),
                            temp["jk"].toString(),
                            temp["kab_kota"].toString(),
                            temp["provinsi"].toString(),
                            temp["tempat_lahir"].toString(),
                            tempFoto,
                            temp["status_pilbupkot"] as Boolean,
                            temp["status_pilgub"] as Boolean,
                            ""
                        )
                        dialog?.dismiss()
                        //camera!!.startPreview()
                        btn_lanjutkan.isEnabled = true
                        btn_lanjutkan.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        btn_lanjutkan.setTextColor(Color.WHITE)
                    } else {
                        alertDialog(
                            "Gagal mengenali E-KTP.\nHarap foto ulang E-KTP Anda",
                            "Identifikasi Gagal"
                        )
                        hasil = ""
                    }
                }
        } else {
            alertDialog(
                "E-KTP tidak ditemukan.\nHarap arahkan kamera Anda pada E-KTP",
                "Error E-KTP"
            )
            hasil = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun checkPermission() {
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        } else {
            /*camera = Camera.open()
            val showCamera: ShowCamera = ShowCamera(this, camera!!)*/
            //frame_camera.addView(showCamera)
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission()
            } else {
                Toast.makeText(this, "Aplikasi perlu menggunakan akses", Toast.LENGTH_LONG).show()
                checkPermission()
            }
        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                "OpenCV",
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            )
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
        checkPermission()
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("MMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? =
            this.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        mcurrentPhotoPath = image.absolutePath
        return image
    }

    //Proses pengenalan dan pemanggilan ML-KIT
    private fun textrecog() {
        /*val image = FirebaseVisionImage.fromBitmap(gg)
        val firebaseVisionTextDetector = FirebaseVision.getInstance().onDeviceTextRecognizer
        firebaseVisionTextDetector.processImage(image)
            .addOnSuccessListener {
                displayTextFromImage(it)
                if (hasil.length == 16 || hasil.length == 15 || hasil.length == 14 || hasil.length == 17) {
                    val docRef = db.collection("users").document(hasil)
                    docRef.get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                var temp = documentSnapshot.data!!
                                Toast.makeText(
                                    this@Identifikasi,
                                    "Proses pengenalan selesai",
                                    Toast.LENGTH_LONG
                                ).show()
                                var tempFoto: ArrayList<String> = ArrayList()
                                for (a in (temp["foto"] as ArrayList<String>)) {
                                    tempFoto.add(a)
                                    Log.d("Pesan", a)
                                }
                                dataExtra = pemilih(
                                    hasil,
                                    temp["nama"].toString(),
                                    temp["agama"].toString(),
                                    temp["jk"].toString(),
                                    temp["kab_kota"].toString(),
                                    temp["provinsi"].toString(),
                                    temp["tempat_lahir"].toString(),
                                    tempFoto,
                                    temp["status_pilbupkot"] as Boolean,
                                    temp["status_pilgub"] as Boolean,
                                    ""
                                )
                                dialog!!.dismiss()
                                //camera!!.startPreview()
                                btn_lanjutkan.isEnabled = true
                                btn_lanjutkan.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                                btn_lanjutkan.setTextColor(Color.WHITE)
                            } else {
                                alertDialog(
                                    "Gagal mengenali E-KTP.\nHarap foto ulang E-KTP Anda",
                                    "Identifikasi Gagal"
                                )
                                hasil = ""
                            }
                        }
                } else {
                    alertDialog(
                        "E-KTP tidak ditemukan.\nHarap arahkan kamera Anda pada E-KTP",
                        "Error E-KTP"
                    )
                    hasil = ""
                }
            }*/
    }

    private fun alertDialog(text: String, title: String) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setMessage(text)
        dialog.setTitle(title)
        dialog.setPositiveButton("Okay", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                //camera!!.startPreview()
                btn_capture.isEnabled = true
            }

        })
        val alert = dialog.create()
        this.dialog?.dismiss()
        alert.show()
    }

    //Ambil teks hasil pengenalan dan normalisasi NIK
    var hasil: String = ""
    private fun formatText(sample:Text){
        val result: ArrayList<String> = ArrayList()
        var ctr = 0
        var ctr1 = 0
        var text = ""
        for (block in sample.textBlocks) {
            val blockText = block.text
            text += blockText
            text += "\n"
            ctr1++
            for (line in block.lines) {
                val lineText = line.text
                result.add(lineText)
                ctr++
                if (lineText.trim().length == 6 || lineText.trim().length == 16 || lineText.trim().length == 15 || lineText.trim().length == 14 || lineText.trim().length == 17) {
                    var cek = 0
                    for (a in lineText) {
                        if (a.isLetter()) {
                            cek++
                        }
                    }
                    if (cek < 5) {
                        hasil = lineText.trim()
                    }
                }
            }
        }
        var temp = ""
        for (x in hasil) {
            if (x == 'B' || x == '&') {
                temp += '8'
            } else if (x == 'D' || x == 'O' || x == 'o') {
                temp += '0'
            } else if (x == 'b' || x == 'L') {
                temp += '6'
            } else if (x == 'l' || x == 'J') {
                temp += 1
            } else {
                temp += x
            }
        }
        hasil = temp.trim()
        temp = ""
        for (a in hasil) {
            if (a.isDigit()) {
                temp += a
            }
        }
        hasil = temp.trim()
        //hasil="1271032508180006"
        //textHasil.text = text
        textNIK.text = "Hasil Normalisasi NIK: " + hasil
    }
    /*private fun displayTextFromImage(firebaseVisionText: FirebaseVisionText) {
        var result: ArrayList<String> = ArrayList()
        var ctr = 0
        var ctr1 = 0
        var text = ""
        for (block in firebaseVisionText.textBlocks) {
            val blockText = block.text
            text += blockText
            text += "\n"
            ctr1++
            for (line in block.lines) {
                val lineText = line.text
                result.add(lineText)
                ctr++
                if (lineText.trim().length == 16 || lineText.trim().length == 15 || lineText.trim().length == 14 || lineText.trim().length == 17) {
                    var cek = 0
                    for (a in lineText) {
                        if (a.isLetter()) {
                            cek++
                        }
                    }
                    if (cek < 5) {
                        hasil = lineText.trim()
                    }
                }
            }
        }
        var temp = ""
        for (x in hasil) {
            if (x == 'B' || x == '&') {
                temp += '8'
            } else if (x == 'D' || x == 'O' || x == 'o') {
                temp += '0'
            } else if (x == 'b' || x == 'L') {
                temp += '6'
            } else if (x == 'l' || x == 'J') {
                temp += 1
            } else {
                temp += x
            }
        }
        hasil = temp.trim()
        temp = ""
        for (a in hasil) {
            if (a.isDigit()) {
                temp += a
            }
        }
        hasil = temp.trim()
        textHasil.text = text
        textNIK.text = "Hasil Normalisasi NIK: " + hasil
    }*/


    val mPictureCallBack = object : Camera.PictureCallback {
        override fun onPictureTaken(data: ByteArray?, cam: Camera?) {
            //fileS = createImageFile()
            /*if (fileS != null) {
                try {
                    var fos = FileOutputStream(fileS)
                    fos.write(data)
                    fos.close()
                    var bitmap = BitmapFactory.decodeFile(fileS!!.absolutePath)
                    var ei: ExifInterface = ExifInterface(fileS!!.absolutePath)
                    var orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )

                    var rotatedBitmap: Bitmap? = null
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> {
                            rotatedBitmap = rotateImage(bitmap, 90f)
                            Log.d("Orientation", "90F")
                        }
                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            rotatedBitmap = rotateImage(bitmap, 180f)
                            Log.d("Orientation", "1800F")
                        }
                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            rotatedBitmap = rotateImage(bitmap, 270f)
                            Log.d("Orientation", "270F")
                        }
                        ExifInterface.ORIENTATION_NORMAL -> {
                            rotatedBitmap = bitmap
                            Log.d("Orientation", "Normal")
                        }
                    }
                    //val x: File = createImageFile()
                    val imageByteArray: ByteArrayOutputStream = ByteArrayOutputStream()
                    rotatedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray)
                    val imageData: ByteArray = imageByteArray.toByteArray()
                    setDpi(imageData, 0)
                    fos = FileOutputStream(fileS)
                    fos.write(imageData)
                    fos.flush()
                    fos.close()
                    var new: Bitmap? = null
                    new = BitmapFactory.decodeFile(fileS!!.absolutePath)
                    gg = new
                    textrecog()

                    //Calculate aspect ratio
                    var koefX = rotatedBitmap!!.width.toFloat() / frame_camera.width
                    var koefY = rotatedBitmap.height.toFloat() / frame_camera.height

                    var x1 = view_border.left
                    var y1 = view_border.top
                    var x2 = view_border.width
                    var y2 = view_border.height
                    var cropStartX = Math.round(x1 * koefX)
                    var cropStartY = Math.round(y1 * koefY)
                    var cropWidthX = Math.round(x2 * koefX)
                    var cropHeightY = Math.round(y2 * koefY)
                    var croppedBitmap: Bitmap? = null
                    if (cropStartX + cropWidthX <= rotatedBitmap.width && cropStartY
                        + cropHeightY <= rotatedBitmap.height
                    ) {
                        croppedBitmap = Bitmap.createBitmap(
                            rotatedBitmap,
                            cropStartX,
                            cropStartY,
                            cropWidthX,
                            cropHeightY
                        )

                    }
                    //Konversi Grayscale
                    val testBmp = croppedBitmap!!.copy(Bitmap.Config.ARGB_8888, false)
                    val bitmapGray1 =
                        Mat(croppedBitmap!!.width, croppedBitmap.height, CvType.CV_8UC1)
                    Utils.bitmapToMat(testBmp, bitmapGray1)
                    Imgproc.cvtColor(bitmapGray1, bitmapGray1, Imgproc.COLOR_BGR2GRAY)
                    Utils.matToBitmap(bitmapGray1, testBmp)
                    //========================================================================================

                    //Segmentasi thresholding
                    ivGrayscaled.setImageBitmap(testBmp)
                    val bitmapGray =
                        Mat(croppedBitmap!!.width, croppedBitmap.height, CvType.CV_8UC1)
                    Utils.bitmapToMat(croppedBitmap, bitmapGray)
                    val bmpBW = croppedBitmap.copy(Bitmap.Config.ARGB_8888, false)
                    Imgproc.cvtColor(bitmapGray, bitmapGray, Imgproc.COLOR_BGR2GRAY)
                    val threshold = 128.0
                    Imgproc.threshold(
                        bitmapGray,
                        bitmapGray,
                        threshold,
                        255.0,
                        Imgproc.THRESH_BINARY
                    )
                    Utils.matToBitmap(bitmapGray, bmpBW)
                    ivblackWhite.setImageBitmap(bmpBW)
                    //=======================================   ==================================================
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }*/

        }

    }

    private fun setDpi(imageData: ByteArray, dpi: Int) {
        imageData[13] = 1
        imageData[14] = (dpi shr 8).toByte()
        imageData[15] = (dpi and 0xff).toByte()
        imageData[16] = (dpi shr 8).toByte()
        imageData[17] = (dpi and 0xff).toByte()
    }

    private fun rotateImage(bitmap: Bitmap?, i: Float): Bitmap? {
        var matrix = Matrix()
        matrix.postRotate(i)
        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

    }

    fun captureImg(view: View) {
        /*if (camera != null) {
            dialog = ProgressDialog(this@Identifikasi)
            dialog!!.setMessage("Mengidentifikasi E-KTP...")
            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            dialog!!.show()
            btn_capture.isEnabled = false
            //camera!!.takePicture(null, null, mPictureCallBack)
        }*/
        dialog = ProgressDialog(this@Identifikasi)
        dialog?.let{
            it.setMessage("Mengidentifikasi E-KTP...")
            it.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            it.show()
            btn_capture.isEnabled = false
            takePicture()
        }
    }


    fun lanjutActivity(view: View) {
        val fragmentManager = supportFragmentManager
        val myFragment = KonfirmasiIdentifikasi()
        val myBundle = Bundle()
        myBundle.putParcelable("data_Extra", dataExtra)
        myFragment.arguments = myBundle
        myFragment.show(fragmentManager, "SimpleFragment")
    }
}