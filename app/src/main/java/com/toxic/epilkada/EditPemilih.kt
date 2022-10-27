package com.toxic.epilkada

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.toxic.epilkada.com.toxic.epilkada.fotoPemilih
import com.toxic.epilkada.com.toxic.epilkada.utils.BottomSheetImageChooser
import kotlinx.android.synthetic.main.activity_add_pemilih.*
import kotlinx.android.synthetic.main.activity_edit_pemilih.*
import kotlinx.android.synthetic.main.activity_edit_pemilihan.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditPemilih : AppCompatActivity() {

    var PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    final val PERMISSION_ALL: Int = 1
    val jenisNegara = arrayOf(
        "WNI",
        "WNA"
    )
    var negara = ""
    val goldar = arrayOf(
        "A", "B", "O", "AB"
    )
    var gol_darah = ""
    val jk = arrayOf("Laki-laki", "Perempuan")
    var jenisKelamin = ""
    val kawin = arrayOf(
        "Belum Kawin", "Sudah Kawin"
    )
    var statusKawin = ""
    val pekerjaan = arrayOf(
        "Pelajar/Mahasiswa",
        "Wiraswasta",
        "Wirausaha"
    )
    var kerja = ""
    val agama = arrayOf(
        "Hindu",
        "Buddha",
        "Islam",
        "Katolik",
        "Kristen",
        "Khonghucu"
    )
    var nilaiAgama = ""
    var date: Date? = null
    lateinit var listFoto : fotoPemilih
    var indexRemoved : ArrayList<Int> = ArrayList()
    var cekImageData=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pemilih)
        spinnerAgamaPemilih.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            agama
        )
        spinnerAgamaPemilih.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                nilaiAgama = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerGoldarPemilih.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            goldar
        )
        spinnerGoldarPemilih.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                gol_darah = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerJKPemilih.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            jk
        )
        spinnerJKPemilih.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                jenisKelamin = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerPekerjaanPemilih.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            pekerjaan
        )
        spinnerPekerjaanPemilih.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    kerja = p0!!.getItemAtPosition(p2).toString()
                }

            }
        spinnerKawinPemilih.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            kawin
        )
        spinnerKawinPemilih.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                statusKawin = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerNegaraPemilih.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            jenisNegara
        )
        spinnerNegaraPemilih.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                negara = p0!!.getItemAtPosition(p2).toString()
            }
        }

        edTanggalPemilih.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val month: Int = calendar.get(Calendar.MONTH)
            val year: Int = calendar.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edTanggalPemilih.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    //val dataFormat=SimpleDateFormat("yyyy/MM/dd")
                    val dataFormat = SimpleDateFormat("dd/MM/yyyy")
                    date = dataFormat.parse(edTanggalPemilih.text.toString())
                    Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show()
                }, year, month, day
            )
            picker.show()
        }
        checkPermission()
    }

    var stringFotoLama = ArrayList<String>()
    var namaLama = ""
    var alamatLama = ""
    var tglLama = ""
    var tptLahirLama = ""
    var kecamatanLama = ""
    var keldeLama = ""
    var kotaLama = ""
    var provLama = ""
    var rtRwLama = ""

    var statusPilbupKot = false
    var statusPilgub = false
    private fun loadData() {
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Mempersiapkan Data...\nProses membutuhkan jaringan internet")
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog!!.show()
        val ExtraData = this.intent.getStringExtra("EXTRA_ID")
        FirebaseFirestore.getInstance().collection("users").document(ExtraData!!)
            .get().addOnSuccessListener {
                edNIKPemilih.hint = it["nik"].toString()
                edNIKPemilih.isEnabled = false
                edNIKPemilih.isClickable = false
                statusPilbupKot = it["status_pilbupkot"] as Boolean
                statusPilgub = it["status_pilgub"] as Boolean
                agamaLama = it["agama"].toString()
                tvAgamaLama.text = "Agama -> " + it["agama"].toString()
                goldarBaru = it["gol_darah"].toString()
                if (goldarBaru.isNullOrEmpty()) {
                    goldarBaru = ""
                }
                Log.d("Goldar", it["gol_darah"].toString())
                tvGoldarLama.text = "Gol.Darah Lama-> " + goldarBaru
                jkBaru = it["jk"].toString()
                tvJKLama.text = "JK Lama-> " + it["jk"].toString()
                statuskawin = it["status_kawin"].toString()
                tvKawinLama.text = "Status Kawin Lama-> " + it["status_kawin"].toString()
                kerjaBaru = it["pekerjaan"].toString()
                tvKerjaLama.text = "Pekerjaan Lama-> " + it["pekerjaan"].toString()
                negaraBaru = it["kewarganegaraan"].toString()
                tvNegaraLama.text = "Kewarganegaraan Lama-> " + it["kewarganegaraan"].toString()
                stringFotoLama = it["foto"] as ArrayList<String>
                edNamaPemilih.hint = it["nama"].toString()
                namaLama = it["nama"].toString()
                edAlamatPemilih.hint = it["alamat"].toString()
                alamatLama = it["alamat"].toString()
                var temp = it["tgl_lahir"] as Timestamp
                tglLama = SimpleDateFormat("dd/MMM/yyyy").format(temp.toDate())
                edTanggalPemilih.setText(tglLama)
                date = temp.toDate()
                edTempatLahirPemilih.hint = it["tempat_lahir"].toString()
                tptLahirLama = it["tempat_lahir"].toString()
                ed_kecamatanPemilih.hint = it["kecamatan"].toString()
                kecamatanLama = it["kecamatan"].toString()
                ed_keldesPemilih.hint = it["kel_desa"].toString()
                keldeLama = it["kel_desa"].toString()
                ed_kotaPemilih.hint = it["kab_kota"].toString()
                kotaLama = it["kab_kota"].toString()
                ed_provinsiPemilih.hint = it["provinsi"].toString()
                provLama = it["provinsi"].toString()
                ed_rtrwPemilih.hint = it["rt_rw"].toString()
                rtRwLama = it["rt_rw"].toString()
                nilaiAgama = ""
                gol_darah = ""
                negara = ""
                statusKawin = ""
                kerja = ""
                tvHitungFotoLama.text = "Dipilih: " + stringFotoLama.size.toString() + " Foto"
                jenisKelamin = ""
                var gg: Bitmap? = null

                //Function untuk mendownload data foto dari firebase storage
                val firebaseStorage = FirebaseStorage.getInstance()
                var mStorageRef: StorageReference? = null
                if(stringFotoLama.size>0){
                    for (a in stringFotoLama) {
                        mStorageRef = firebaseStorage.getReferenceFromUrl(a)
                        mStorageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { img->
                            gg = BitmapFactory.decodeByteArray(img, 0, img.size)
                            //gg?.let { decodedBmp-> trainImage(decodedBmp) }
                            fotoLama!!.add(gg!!)
                            Log.d("Berhasil", "Berhasil buka")
                            if (fotoLama.size == stringFotoLama.size) {
                                dialog!!.dismiss()
                            }
                        }.addOnFailureListener {

                        }
                    }
                }else{
                    Log.d("Berhasil", "Berhasil buka")
                    dialog!!.dismiss()
                }
            }
    }

    val fotoLama: ArrayList<Bitmap>? = ArrayList()
    var agamaLama = ""
    var goldarBaru = ""
    var negaraBaru = ""
    var statuskawin = ""
    var kerjaBaru = ""
    var jkBaru = ""
    fun updateData(view: View) {
        if (!nilaiAgama.isBlank()) {
            agamaLama = nilaiAgama
        }
        if (!gol_darah.isBlank()) {
            goldarBaru = gol_darah
        }
        if (!negara.isBlank()) {
            negaraBaru = negara
        }
        if (!statusKawin.isBlank()) {
            statuskawin = statusKawin
        }
        if (!kerja.isBlank()) {
            kerjaBaru = kerja
        }
        if (!jenisKelamin.isBlank()) {
            jkBaru = jenisKelamin
        }
        if (edNamaPemilih.text.isNotBlank()) {
            namaLama = edNamaPemilih.text.toString()
        }
        if (edAlamatPemilih.text.isNotBlank()) {
            alamatLama = edAlamatPemilih.text.toString()
        }
        if (edTempatLahirPemilih.text.isNotBlank()) {
            tptLahirLama = edTempatLahirPemilih.text.toString()
        }
        if (ed_kecamatanPemilih.text.isNotBlank()) {
            kecamatanLama = ed_kecamatanPemilih.text.toString()
        }
        if (ed_keldesPemilih.text.isNotBlank()) {
            keldeLama = ed_keldesPemilih.text.toString()
        }
        if (ed_rtrwPemilih.text.isNotBlank()) {
            rtRwLama = ed_rtrwPemilih.text.toString()
        }
        if (ed_kotaPemilih.text.isNotBlank()) {
            kotaLama = ed_kotaPemilih.text.toString()
        }
        if (ed_provinsiPemilih.text.isNotBlank()) {
            provLama = ed_provinsiPemilih.text.toString()
        }

        Log.d("CekData", gol_darah + nilaiAgama)
        val imageByte: ArrayList<ByteArray> = ArrayList()
        buatDialog("Menyimpan Data.\n Proses membutuhkan jaringan internet..")
        //train old photos
        /*if(fotoLama!=null){
            for(foto in fotoLama){
                trainImage(foto)
            }
        }*/
        //=====================
        if (!imageData.isNullOrEmpty()) {
            imageData?.let{
                doAsync {
                    for (i in 0 until it.size) {
                        val imageByteArray: ByteArrayOutputStream = ByteArrayOutputStream()
                        it[i].compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray)
                        imageByte.add(imageByteArray.toByteArray())
                        Log.d("index i", i.toString())
                    }
                    uiThread {
                        Log.d("jalankan firebase", "firebaseee")
                        addFirebaseData(imageByte)
                    }
                }
            }

        }
        else{
            addFirebaseData(imageByte)
        }
    }

    fun trainImage(bitmap:Bitmap){
        val detector = FaceDetection.getClient()
        val inputImage = InputImage.fromBitmap(bitmap,0)
        var result : Bitmap?=null
        detector.process(inputImage)
            .addOnSuccessListener { faces->
                if(faces.isNullOrEmpty()){
                    Toast.makeText(this,"Wajah tidak ditemukan, harap arahkan kamera ke wajah Anda.",Toast.LENGTH_LONG).show()
                }
                else{
                    for(face in faces){
                        val bound = face.boundingBox
                        try{
                            result = Bitmap.createBitmap(
                                bitmap,
                                bound.left,
                                bound.top,
                                bound.width(),
                                bound.height()
                            )
                            var temp: Bitmap? = null
                            temp = Bitmap.createScaledBitmap(result!!, 180, 200, false)
                            imageData.add(temp)
                            if(imageData.size==(fotoLama?.size?:0)-1){
                                val imageByte: ArrayList<ByteArray> = ArrayList()
                                imageData.let{
                                    doAsync {
                                        for (i in 0 until it.size) {
                                            val imageByteArray: ByteArrayOutputStream = ByteArrayOutputStream()
                                            it[i].compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray)
                                            imageByte.add(imageByteArray.toByteArray())
                                            Log.d("index i", i.toString())
                                        }
                                        uiThread {
                                            Log.d("jalankan firebase", "firebaseee")
                                            addFirebaseData(imageByte)
                                        }
                                    }
                                }
                            }
                            /*fotoLama!!.add(result!!)
                            Log.d("Berhasil", "Berhasil buka")
                            if (fotoLama.size == stringFotoLama.size) {
                                dialog!!.dismiss()
                            }*/
                        }catch (exc:Exception){
                            Log.e("EditPemilih",exc.localizedMessage,exc)
                        }

                        /*Log.d("FaceDetection",bound.toString())
                        Log.d(
                            "UkuranCropped",
                            croppedBitmap!!.width.toString() + "x" + croppedBitmap.height.toString()
                        )
                        result = Bitmap.createScaledBitmap(croppedBitmap, 180, 200, false)*/
                    }
                }

            }
            .addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
    }

    private fun addFirebaseData(imageByte: ArrayList<ByteArray>) {
        if (!imageByte.isNullOrEmpty()) {
            val stringFoto: ArrayList<String> = ArrayList()
            for (i in 0 until imageByte.size) {
                val imageRef =
                    FirebaseStorage.getInstance().getReferenceFromUrl("gs://epilkada.appspot.com/")
                        .child(edNamaPemilih.text.toString() + "_" + edNIKPemilih.hint.toString() + "/" + edNamaPemilih.text.toString() + (i + 1).toString())
                imageRef.putBytes(imageByte[i]).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        stringFoto.add(it.result.toString())
                        stringFotoLama.add(it.result.toString())
                        if (stringFoto.size == imageByte.size) {
                            if(!indexRemoved.isNullOrEmpty()){
                                for(i in indexRemoved){
                                    stringFotoLama.removeAt(i)
                                }
                            }
                            val dataBaru = hashMapOf(
                                "nik" to edNIKPemilih.hint.toString(),
                                "nama" to namaLama,
                                "agama" to agamaLama,
                                "alamat" to alamatLama,
                                "jk" to jkBaru,
                                "gol_darah" to goldarBaru,
                                "kecamatan" to kecamatanLama,
                                "kel_desa" to keldeLama,
                                "kewarganegaraan" to negaraBaru,
                                "pekerjaan" to kerjaBaru,
                                "rt_rw" to rtRwLama,
                                "status_kawin" to statuskawin,
                                "status_pilbupkot" to statusPilbupKot,
                                "status_pilgub" to statusPilgub,
                                "tempat_lahir" to tptLahirLama,
                                "tgl_lahir" to date,
                                "foto" to stringFotoLama,
                                "kab_kota" to kotaLama,
                                "provinsi" to provLama
                            )
                            FirebaseFirestore.getInstance().collection("users")
                                .document(edNIKPemilih.hint.toString())
                                .set(dataBaru).addOnSuccessListener {
                                    dialog!!.dismiss()
                                    val dialog1: AlertDialog.Builder = AlertDialog.Builder(this)
                                    dialog1.setMessage("Berhasil menyimpan Data.")
                                    dialog1.setTitle("Proses Berhasil")
                                    dialog1.setPositiveButton(
                                        "Okay",
                                        object : DialogInterface.OnClickListener {
                                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                                this@EditPemilih.finish()
                                            }
                                        })
                                    val alert = dialog1.create()
                                    alert.show()

                                }
                        }
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            }
        } else {
            if(!indexRemoved.isNullOrEmpty()){
                for(i in indexRemoved){
                    stringFotoLama.removeAt(i)
                    Log.d("RemoveAt",i.toString())
                }
            }
            val dataBaru = hashMapOf(
                "nik" to edNIKPemilih.hint.toString(),
                "nama" to namaLama,
                "agama" to agamaLama,
                "alamat" to alamatLama,
                "jk" to jkBaru,
                "gol_darah" to goldarBaru,
                "kecamatan" to kecamatanLama,
                "kel_desa" to keldeLama,
                "kewarganegaraan" to negaraBaru,
                "pekerjaan" to kerjaBaru,
                "rt_rw" to rtRwLama,
                "status_kawin" to statuskawin,
                "status_pilbupkot" to statusPilbupKot,
                "status_pilgub" to statusPilgub,
                "tempat_lahir" to tptLahirLama,
                "tgl_lahir" to date,
                "foto" to stringFotoLama,
                "kab_kota" to kotaLama,
                "provinsi" to provLama
            )
            FirebaseFirestore.getInstance().collection("users")
                .document(edNIKPemilih.hint.toString())
                .set(dataBaru).addOnSuccessListener {
                    dialog!!.dismiss()
                    val dialog1: AlertDialog.Builder = AlertDialog.Builder(this)
                    dialog1.setMessage("Berhasil menyimpan Data.")
                    dialog1.setTitle("Proses Berhasil")
                    dialog1.setPositiveButton(
                        "Okay",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                this@EditPemilih.finish()
                            }
                        })
                    val alert = dialog1.create()
                    alert.show()
                }
        }

    }



    //Function untuk melihat foto yang sudah diambil oleh admin
    fun lihatFoto(view: View) {
        val fragmentManager = this.supportFragmentManager
        if (!imageData.isNullOrEmpty() && cekImageData==false) {
            for (i in imageData!!) {
                fotoLama!!.add(i)
            }
            cekImageData=true
        }
        listFoto = fotoPemilih(fotoLama!!,indexRemoved)
        val fragmentDetail = lihatFotoPemilih()
        val myBundle = Bundle()
        myBundle.putParcelable("BUNDLE_FOTO", listFoto)
        fragmentDetail.arguments = myBundle
        fragmentDetail.show(fragmentManager, "Fragment Detail")
    }

    //Function untuk membuka galeri admin
    var ambilGambar = false
    var imageData: ArrayList<Bitmap> = ArrayList()
    fun pilihFoto(view: View){
        val modalBottomSheet = BottomSheetImageChooser()
        modalBottomSheet.show(supportFragmentManager,null)
        modalBottomSheet.setOnGetPhotosCallback(object:BottomSheetImageChooser.GetPhotosCallback{
            override fun onGetPhotos(photoUris: List<Uri>) {
                /*for (i in 0 until data.clipData!!.itemCount) {
                    val inStream = applicationContext.contentResolver.openInputStream(
                        data.clipData!!.getItemAt(i).uri
                    )
                    val bmp = BitmapFactory.decodeStream(inStream)
                    imageData!!.add(rotateImage(bmp, 90f)!!)
                    inStream!!.close()
                }*/
                buatDialog("Mempersiapkan Foto...")
                var success=0
                for(i in photoUris.indices){
                    val inStream = applicationContext.contentResolver.openInputStream(
                        photoUris[i]
                    )
                    val bmp = BitmapFactory.decodeStream(inStream)
                    val normalizedBmp=rotateImage(bmp,0)
                    //imageData.add(rotateImage(bmp,0))
                    inStream?.close()
                    val detector = FaceDetection.getClient()
                    val inputImage = InputImage.fromBitmap(normalizedBmp,0)
                    var result : Bitmap?=null
                    detector.process(inputImage)
                        .addOnSuccessListener { faces->
                            if(faces.isNullOrEmpty()){
                                //Toast.makeText(this,"Wajah tidak ditemukan, harap arahkan kamera ke wajah Anda.",Toast.LENGTH_LONG).show()
                            }
                            else{
                                success++
                                for(face in faces){
                                    val bound = face.boundingBox
                                    try{
                                        result = Bitmap.createBitmap(
                                            normalizedBmp,
                                            bound.left,
                                            bound.top,
                                            bound.width(),
                                            bound.height()
                                        )
                                        var temp: Bitmap? = null
                                        temp = Bitmap.createScaledBitmap(result!!, 180, 200, false)
                                        imageData.add(temp)
                                    }catch (exc:Exception){
                                        Log.e("EditPemilih",exc.localizedMessage,exc)
                                    }
                                }
                            }
                            if(i==photoUris.size-1){
                                Toast.makeText(this@EditPemilih,"Berhasil mendeteksi wajah dari $success/${photoUris.size} foto",Toast.LENGTH_LONG).show()
                                ambilGambar = true
                                val jlhFotoBaru = fotoLama!!.size + imageData!!.size
                                tvHitungFotoLama!!.text = "Dipilih: " + jlhFotoBaru + " Foto"
                                cekImageData=false
                                dialog!!.dismiss()
                            }

                        }
                        .addOnFailureListener {
                            Toast.makeText(this@EditPemilih,it.localizedMessage,Toast.LENGTH_LONG).show()
                            if(i==photoUris.size-1){
                                ambilGambar = true
                                val jlhFotoBaru = fotoLama!!.size + imageData!!.size
                                tvHitungFotoLama!!.text = "Dipilih: " + jlhFotoBaru + " Foto"
                                cekImageData=false
                                dialog!!.dismiss()
                            }
                        }
                }
                /*Toast.makeText(
                    applicationContext,
                    "Berhasil mendapatkan foto " + imageData!!.size,
                    Toast.LENGTH_SHORT
                ).show()*/

            }

        })
    }

    /*var ambilGambar = false
    var imageData: ArrayList<Bitmap> = ArrayList()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.clipData != null) {
            buatDialog("Mengambil Foto...")
            imageData = ArrayList()
            Log.d("Ndata", data.clipData.toString())

            //Function ambil data dari galeri admin
            doAsync {
                for (i in 0 until data.clipData!!.itemCount) {
                    val inStream = applicationContext.contentResolver.openInputStream(
                        data.clipData!!.getItemAt(i).uri
                    )
                    val bmp = BitmapFactory.decodeStream(inStream)
                    imageData!!.add(rotateImage(bmp, 90f)!!)
                    inStream!!.close()
                }
                uiThread {
                    ambilGambar = true
                    Toast.makeText(
                        applicationContext,
                        "Berhasil mendapatkan foto " + imageData!!.size,
                        Toast.LENGTH_SHORT
                    ).show()
                    val jlhFotoBaru = fotoLama!!.size + imageData!!.size
                    tvHitungFotoLama!!.text = "Dipilih: " + jlhFotoBaru + " Foto"
                    cekImageData=false
                    dialog!!.dismiss()
                }
            }
        }
    }*/

    private var dialog: ProgressDialog? = null
    fun buatDialog(text: String) {
        dialog = ProgressDialog(this)
        dialog!!.setMessage(text)
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog!!.show()
    }

    private fun rotateImage(bitmap: Bitmap?, i: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(i)
        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

    }


    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission() {
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
        else{
            loadData()
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

}