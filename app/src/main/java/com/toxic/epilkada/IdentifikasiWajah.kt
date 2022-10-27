package com.toxic.epilkada

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.hardware.Camera
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.activity_identifikasi_wajah.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Core.NORM_MINMAX
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.face.EigenFaceRecognizer
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class IdentifikasiWajah : AppCompatActivity() {

    private var mat: Mat? = null
    lateinit var traindata: ArrayList<Mat>
    private var mStorageRef: StorageReference? = null
    private var camera: Camera? = null
    lateinit var foto: ArrayList<Bitmap>
    var dialog: ProgressDialog? = null
    private var showCamera: ShowCamera? = null
    var dataPemilih: pemilih? = null
    private var lbl1: Mat? = null
    private var mcurrentPhotoPath = ""
    var fileS: File? = null

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
        setContentView(R.layout.activity_identifikasi_wajah)
        OpenCVLoader.initDebug()
        val intent = intent
        dataPemilih = intent.getParcelableExtra("dataParcel")
        foto = ArrayList()
        var gg: Bitmap? = null
        val ONE_MB: Long = 1024 * 1024
        val firebaseStorage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this@IdentifikasiWajah)
        dialog!!.setMessage("Mempersiapkan Data...\nProses membutuhkan jaringan internet")
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog!!.show()

        //Pengambilan data wajah dari FirebaseStorage
        for (a in dataPemilih!!.foto) {
            mStorageRef = firebaseStorage.getReferenceFromUrl(a)
            mStorageRef!!.getBytes(ONE_MB).addOnSuccessListener {
                gg = BitmapFactory.decodeByteArray(it, 0, it.size)
                foto.add(gg!!)
                Log.d("Berhasil", "Berhasil buka")
                Log.d("UkuranArray1", foto.size.toString())
                if (foto.size == dataPemilih!!.foto.size) {
                    trainingData()
                    bukaKamera()
                }
            }.addOnFailureListener {

            }
        }
        //=========================================================================

        btnNext.setOnClickListener {
            val intent = Intent(this, JenisPemilihan::class.java)
            intent.putExtra("dataExtra", dataPemilih)
            startActivity(intent)
        }
    }

    private fun trainingData() {
        traindata = ArrayList()
        //iv_recognized.setImageBitmap(foto[0])
        for (a in 0 until foto.size) {
            Log.d("UkuranFoto",a.toString())
            val tmp = Mat(foto[0].width, foto[0].height, CvType.CV_8UC1)
            val tmp1 = Mat(foto[0].width, foto[0].height, CvType.CV_8UC1)
            Utils.bitmapToMat(foto[a], tmp)
            Imgproc.cvtColor(tmp, tmp1, Imgproc.COLOR_BGR2GRAY)
            traindata.add(tmp1)
        }
        lbl1 = Mat(traindata.size, 1, CvType.CV_32SC1)
        for (a in 0 until traindata.size) {
            Log.d(
                "UkuranTrain",
                "Foto k-" + a.toString() + foto[a].width.toString() + "x" + foto[a].height.toString()
            )
            lbl1!!.put(a, 0, a.toDouble())
            Log.d("Isi Label2", a.toString())
            //Log.d("Isi Label1",lbl1[a,0].toString())
            val isi = lbl1!!.get(a, 0).component1()
            Log.d("Isi Label3", isi.toString())
        }
        dialog!!.dismiss()
        /*try{
            doAsync {
                traindata = ArrayList()
                iv_recognized.setImageBitmap(foto[0])
                for (a in 0 until foto.size) {
                    Log.d("UkuranFoto",a.toString())
                    val tmp = Mat(foto[0].width, foto[0].height, CvType.CV_8UC1)
                    val tmp1 = Mat(foto[0].width, foto[0].height, CvType.CV_8UC1)
                    Utils.bitmapToMat(foto[a], tmp)
                    Imgproc.cvtColor(tmp, tmp1, Imgproc.COLOR_BGR2GRAY)
                    traindata.add(tmp1)
                }
                lbl1 = Mat(traindata.size, 1, CvType.CV_32SC1)
                for (a in 0 until traindata.size) {
                    Log.d(
                        "UkuranTrain",
                        "Foto k-" + a.toString() + foto[a].width.toString() + "x" + foto[a].height.toString()
                    )
                    lbl1!!.put(a, 0, a.toDouble())
                    Log.d("Isi Label2", a.toString())
                    //Log.d("Isi Label1",lbl1[a,0].toString())
                    val isi = lbl1!!.get(a, 0).component1()
                    Log.d("Isi Label3", isi.toString())
                }
                uiThread {
                    dialog!!.dismiss()
                }
            }
        }catch (exc:Exception){
            Log.e("ErroTrainData",exc.localizedMessage,exc)
        }*/

    }

    override fun onBackPressed() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setMessage("Proses identifikasi akan dibatalkan. \nApakah Anda yakin untuk kembali?")
        dialog.setTitle("Membatalkan Identifikasi")
        dialog.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                camera!!.stopPreview()
                camera!!.release()
                this@IdentifikasiWajah.finish()
            }
        })
        dialog.setNegativeButton("Tidak", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }
        })
        val alert = dialog.create()
        alert.show()
    }


    private fun prosesPengenalan(bitmap: Bitmap) {
        var imageInput = Mat(foto[0].width, foto[0].height, CvType.CV_8UC1)
        Utils.bitmapToMat(bitmap, imageInput)
        Imgproc.cvtColor(imageInput, imageInput, Imgproc.COLOR_RGB2GRAY)
        var bitmap1 = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(imageInput, bitmap1)
        iv_testSample.setImageBitmap(bitmap1)
        var model = Mat()
        imageInput.reshape(0, 1).convertTo(model, CvType.CV_64FC1)

        /*val facerec: EigenFaceRecognizer = EigenFaceRecognizer.create(0, 5300.0)*/
        val facerec: EigenFaceRecognizer = EigenFaceRecognizer.create(0, 3500.0)
        facerec.train(traindata, lbl1)
        var predicted: IntArray = IntArray(2)
        predicted[0] = 0
        var confidence: DoubleArray = DoubleArray(2)
        confidence[0] = 0.0
        facerec.predict(imageInput, predicted, confidence)
        val mean = facerec.mean
        val eigenValue = facerec.eigenValues
        val eigenVector = facerec.eigenVectors
        try {
            val wp = normalisasiFoto(mean!!.reshape(1, traindata[0].rows()))
            val set = Bitmap.createBitmap(wp.width(), wp.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(wp, set)
            iv_mean.setImageBitmap(set)
            Log.d("UkuranAwalMean", wp.size().toString())
            Log.d("Mean", mean.size().toString())
            Log.d("EigenValue", eigenValue!!.size().toString())
            Log.d("EigenVector", eigenVector!!.size().toString())
            val cGrayScale: ArrayList<Mat> = ArrayList()
            for (i in 0 until min(10, eigenVector.cols())) {
                Log.d("EigenValue ke-" + i.toString(), eigenValue[i, 0]!!.contentToString())
                Log.d(
                    "Mean*EigenVector",
                    mean[0, i]!!.contentToString() + eigenVector[i, 0]!!.contentToString()
                )
                val ev = eigenVector.col(i).clone()
                val grayscale = normalisasiFoto(ev.reshape(1, traindata[0].rows()))
                cGrayScale.add(grayscale)
                val bitmp1 = Bitmap.createBitmap(
                    grayscale.width(),
                    grayscale.height(),
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(grayscale, bitmp1)
            }
            Log.d("CgrayscaleSize", cGrayScale.size.toString())

            //Bentuk Training set ROW
            val vectorImageS: ArrayList<Mat> = ArrayList()
            val vectorIm=Mat()
            for (a in 0 until traindata.size) {
                val temp: Mat = traindata[a]
                val temp1: Mat = Mat(mean.size(), CvType.CV_64FC1)
                temp.reshape(0, 1).convertTo(temp1, CvType.CV_64FC1)
                vectorImageS.add(temp1)
                vectorIm.push_back(temp1)
            }
            Log.d("VectorImages", vectorImageS.size.toString())
            //======================================================

            //Test Face - Mean Training
            val imVector = Mat(mean.size(), CvType.CV_64FC1)
            Core.subtract(model, mean, imVector)
            val yuuuhu = normalisasiFoto(imVector.reshape(1, traindata[0].rows()))
            val uuhu = Bitmap.createBitmap(yuuuhu.width(), yuuuhu.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(yuuuhu, uuhu)
            iv_meanTest.setImageBitmap(uuhu)
            //-----------------------------------

            //Nilai A
            val meanSubsEigen: ArrayList<Mat> = ArrayList()
            val nilaiA = Mat()
            for (i in 0 until vectorImageS.size) {
                val temp1 = Mat(mean.size(), CvType.CV_64FC1)
                Core.subtract(vectorImageS[i], mean, temp1)
                meanSubsEigen.add(temp1)
                nilaiA.push_back(temp1)
                Log.d("wELLPLAYED ke-" + i.toString(), meanSubsEigen[i].size().toString())
            }
            val Anormal = normalisasiFoto(meanSubsEigen[5].reshape(1, traindata[0].rows()))
            val aww = Bitmap.createBitmap(Anormal.width(), Anormal.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(Anormal, aww)
            Log.d("NilaiA", nilaiA.size().toString())
            //=========================================================

            //Weight Dataset
            val wDataset = Mat()
            Core.gemm(nilaiA, eigenVector, 1.0, Mat(), 0.0, wDataset)
            //======================================================================


            //Weight DataTest
            var wDataTest = Mat()
            for (i in 0 until wDataset.cols()) {
                var temp = Mat()
                Core.gemm(imVector, eigenVector.col(i), 1.0, Mat(), 0.0, temp)
                wDataTest.push_back(temp)
            }
            //=======================================================================

            val wsetTranspose=Mat()
            Core.transpose(wDataset,wsetTranspose)

            //Hitung Euclidean Distance
            var distance1:ArrayList<Int> = ArrayList()
            for(i in 0 until traindata.size){
                var temp=0.0
                for(j in 0 until traindata.size){
                    var tempGG=wDataTest[j,0].get(0)-wsetTranspose[j,i].get(0)
                    var hasilPangkat=tempGG.pow(2.0)
                    temp += hasilPangkat
                }
                var tempwp=sqrt(temp).toInt()
                distance1.add(tempwp)
                Log.d("Distance1",distance1[i].toString())
            }
            //============================================================================================================

            //Membuat dan menampilkan nilai bobot kedalam diagram batang
            graph_jarak.removeAllSeries()
            val series =
                BarGraphSeries(
                    arrayOf<DataPoint>(
                    )
                )
            series.spacing=50
            series.isAnimated=true
            series.isDrawValuesOnTop=true
            series.valuesOnTopColor= Color.RED
            series.valuesOnTopSize=22.0F
            for(i in 0 until distance1.size){
                series.appendData(DataPoint(i.toDouble(),distance1[i].toDouble()),true,distance1.size)
            }
            graph_jarak.addSeries(series)
            graph_jarak.getViewport().setMinX(0.0)
            graph_jarak.getViewport().setMaxX(distance1.size.toDouble())
            graph_jarak.getViewport().setXAxisBoundsManual(true)
            Log.d("Minimum Graph",series.lowestValueY.toString())

            //=====================================================================
            var indexRecognized=0
            for(i in 0 until distance1.size){
                if(distance1[i]==series.lowestValueY.toInt()){
                    Log.d(
                        "Index Label",
                        i.toString()
                    )
                    indexRecognized=i
                }
            }

            //Menghitung nilai threshold dan menentukan wajah dikenali atau tidak
            Collections.sort(distance1,Collections.reverseOrder())
            var thres = (distance1[0] * 0.5)
            tv_dmax.text = "Distance Max: " + distance1[0].toString()
            tv_dmin.text = "Distance Min: " + distance1[distance1.size-1].toString()
            tvThreshold.text =
                "Nilai threshold: " + distance1[0].toString() + "*0.5 = " + thres.toString()
            if ( thres> distance1[distance1.size-1]) {
                tv_perbandingan.text = "Hasil: \nthreshold > Dmin = Wajah dikenali"
                btnNext.isEnabled = true
                btnNext.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                iv_recognized.setImageBitmap(foto[indexRecognized])
                camera!!.startPreview()
                btn_Foto.isEnabled = true
                dialog!!.dismiss()
                Toast.makeText(this,"Wajah dikenali",Toast.LENGTH_SHORT).show()
            } else {
                iv_recognized.setImageResource(R.drawable.unknown_face)
                tv_perbandingan.text = "Hasil: \nthreshold < Dmin = Wajah tidak dikenali"
                btnNext.isEnabled = false
                dialog!!.dismiss()
                camera!!.startPreview()
                btn_Foto.isEnabled = true
                btnNext.setBackgroundColor(Color.GRAY)
                Toast.makeText(this,"Wajah tidak dikenali, harap coba lagi!",Toast.LENGTH_SHORT).show()
            }
            //================================================================================

        } catch (e: Exception) {
            btnNext.isEnabled = false
            dialog!!.dismiss()
            camera!!.startPreview()
            btn_Foto.isEnabled = true
            e.printStackTrace()
        }
    }


    fun normalisasiFoto(mat: Mat): Mat {
        var src = mat
        var dst = Mat()
        if (src.channels() == 1) {
            Core.normalize(src, dst, 0.0, 255.0, NORM_MINMAX, CvType.CV_8UC1)
        } else if (src.channels() == 3) {
            Core.normalize(src, dst, 0.0, 255.0, NORM_MINMAX, CvType.CV_8UC3)
        } else {
            src.copyTo(dst)
        }
        return dst
    }

    private fun bukaKamera() {
        var cameraCount = 0
        var cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        for (a in 0 until cameraCount) {
            Camera.getCameraInfo(a, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    camera = Camera.open(a)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        showCamera = ShowCamera(this, camera!!)
        frame_identifikasi.addView(showCamera)
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
        if (foto.size == dataPemilih!!.foto.size) {
            camera = Camera.open(1)
            showCamera = ShowCamera(this, camera!!)
            frame_identifikasi.addView(showCamera)
        }
    }


    val mPictureCallBack = object : Camera.PictureCallback {
        override fun onPictureTaken(data: ByteArray?, cam: Camera?) {
            fileS = createImageFile()
            if (fileS != null) {
                try {
                    val fos = FileOutputStream(fileS)
                    fos.write(data)
                    fos.close()
                    val bitmap = BitmapFactory.decodeFile(fileS!!.absolutePath)
                    val ei: ExifInterface = ExifInterface(fileS!!.absolutePath)
                    val orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )

                    //iv_Test.setImageBitmap(bitmap)
                    var rotatedBitmap: Bitmap? = null
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> {
                            rotatedBitmap = rotateImage(bitmap, 90f)
                            Log.d("Orientation", "90F")
                        }
                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            rotatedBitmap = rotateImage(bitmap, 180f)
                            Log.d("Orientation", "180F")
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


                    //Crop Foto berdasarkan kotak merah
                    /*val koefX = rotatedBitmap!!.width.toFloat() / frame_identifikasi.width
                    val koefY = rotatedBitmap.height.toFloat() / frame_identifikasi.height

                    val x1 = view_borderWajah.left
                    val y1 = view_borderWajah.top
                    val x2 = view_borderWajah.width
                    val y2 = view_borderWajah.height
                    val cropStartX = Math.round(x1 * koefX)
                    val cropStartY = Math.round(y1 * koefY)
                    val cropWidthX = Math.round(x2 * koefX)
                    val cropHeightY = Math.round(y2 * koefY)*/
                    var croppedBitmap: Bitmap? = null
                    /*if (cropStartX + cropWidthX <= rotatedBitmap.width && cropStartY
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
                    Log.d(
                        "UkuranCropped",
                        croppedBitmap!!.width.toString() + "x" + croppedBitmap.height.toString()
                    )*/
                    val detector = FaceDetection.getClient()
                    val inputImage = InputImage.fromBitmap(rotatedBitmap!!,0)
                    detector.process(inputImage)
                        .addOnSuccessListener {
                            if(it.isNullOrEmpty()){
                                iv_recognized.setImageResource(R.drawable.unknown_face)
                                btnNext.isEnabled = false
                                dialog!!.dismiss()
                                camera!!.startPreview()
                                btn_Foto.isEnabled = true
                                btnNext.setBackgroundColor(Color.GRAY)
                                Toast.makeText(this@IdentifikasiWajah,"Wajah tidak ditemukan, harap arahkan kamera ke wajah Anda.",Toast.LENGTH_LONG).show()
                            }
                            else{
                                for(face in it){
                                    val bound = face.boundingBox
                                    croppedBitmap = Bitmap.createBitmap(
                                        rotatedBitmap!!,
                                        bound.left,
                                        bound.top,
                                        bound.width(),
                                        bound.height()
                                    )
                                    Log.d("FaceDetection",bound.toString())
                                    Log.d(
                                        "UkuranCropped",
                                        croppedBitmap!!.width.toString() + "x" + croppedBitmap!!.height.toString()
                                    )
                                    var temp: Bitmap? = null
                                    temp = Bitmap.createScaledBitmap(croppedBitmap!!, 180, 200, false)
                                    iv_croppedImage.setImageBitmap(temp)
                                    Log.d("Ukuran", temp.width.toString() + "x" + temp.height.toString())
                                    prosesPengenalan(temp)
                                }
                            }

                        }
                        .addOnFailureListener {
                            iv_recognized.setImageResource(R.drawable.unknown_face)
                            btnNext.isEnabled = false
                            dialog!!.dismiss()
                            camera!!.startPreview()
                            btn_Foto.isEnabled = true
                            btnNext.setBackgroundColor(Color.GRAY)
                            Toast.makeText(this@IdentifikasiWajah,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    /*var temp: Bitmap? = null
                    temp = Bitmap.createScaledBitmap(croppedBitmap, 180, 200, false)
                    iv_croppedImage.setImageBitmap(temp)
                    Log.d("Ukuran", temp.width.toString() + "x" + temp.height.toString())
                    prosesPengenalan(temp)*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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

    private fun rotateImage(bitmap: Bitmap?, i: Float): Bitmap? {
        var matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f);
        matrix.postRotate(i)
        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

    }

    fun onCapture(view: View) {
        if (camera != null) {
            dialog!!.setMessage("Mengidentifikasi Wajah...")
            dialog!!.show()
            btn_Foto.isEnabled = false
            camera!!.takePicture(null, null, mPictureCallBack)
        }
    }
}