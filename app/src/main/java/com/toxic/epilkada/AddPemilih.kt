package com.toxic.epilkada

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.toxic.epilkada.com.toxic.epilkada.fotoPemilih
import kotlinx.android.synthetic.main.activity_add_pemilih.*
import kotlinx.android.synthetic.main.activity_add_pemilih.btnTambahkan
import kotlinx.android.synthetic.main.activity_add_pemilih.edTanggal
import kotlinx.android.synthetic.main.activity_add_pemilih.iv_image
import kotlinx.android.synthetic.main.activity_add_pemilih.spinnerAgama
import kotlinx.android.synthetic.main.activity_add_pemilih.spinnerGoldar
import kotlinx.android.synthetic.main.activity_add_pemilih.spinnerJK
import kotlinx.android.synthetic.main.activity_add_pemilih.spinnerKawin
import kotlinx.android.synthetic.main.activity_add_pemilih.spinnerNegara
import kotlinx.android.synthetic.main.activity_add_pemilih.spinnerPekerjaan
import kotlinx.android.synthetic.main.activity_add_pemilih.tvHitungFoto
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddPemilih : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pemilih)
        spinnerAgama.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            agama
        )
        spinnerAgama.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                nilaiAgama = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerGoldar.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            goldar
        )
        spinnerGoldar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                gol_darah = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerJK.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            jk
        )
        spinnerJK.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                jenisKelamin = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerPekerjaan.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            pekerjaan
        )
        spinnerPekerjaan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                kerja = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerKawin.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            kawin
        )
        spinnerKawin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                statusKawin = p0!!.getItemAtPosition(p2).toString()
            }

        }
        spinnerNegara.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            jenisNegara
        )
        spinnerNegara.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                negara = p0!!.getItemAtPosition(p2).toString()
            }

        }
        iv_image.setOnClickListener {
            ambilFoto()
        }
        tvHitungFoto.setOnClickListener {
            lihatFoto()
        }
        edTanggal.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            var day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            var month: Int = calendar.get(Calendar.MONTH)
            var year: Int = calendar.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edTanggal.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    val dataFormat = SimpleDateFormat("dd/MM/yyyy")
                    date = dataFormat.parse(edTanggal.text.toString())
                    Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show()
                }, year, month, day
            )
            picker.show()
        }

        btnTambahkan.setOnClickListener {
            uploadData()
        }
    }


    private fun uploadData() {
        if (edNIK.text.isNotBlank() && edNama.text.isNotBlank() && jenisKelamin.isNotBlank() && edTempatLahir.text.isNotBlank() && date != null && edAlamat.text.isNotBlank() && ed_rtrw.text.isNotBlank() && ed_keldes.text.isNotBlank() && ed_kecamatan.text.isNotBlank() && ed_kota.text.isNotBlank() && ed_provinsi.text.isNotBlank() && nilaiAgama.isNotBlank() && statusKawin.isNotBlank() && kerja.isNotBlank() && negara.isNotBlank()) {
            if (imageData!!.size >= 10) {
                buatDialog("Menyimpan Data.\n Proses membutuhkan jaringan internet..")
                var imageByte: ArrayList<ByteArray> = ArrayList()
                doAsync {
                    for (i in 0 until imageData!!.size) {
                        val imageByteArray: ByteArrayOutputStream = ByteArrayOutputStream()
                        imageData!![i].compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray)
                        imageByte.add(imageByteArray.toByteArray())
                        Log.d("index i", i.toString())
                    }
                    uiThread {
                        Log.d("jalankan firebase", "firebaseee")
                        addFirebaseData(imageByte)
                    }
                }
            } else {
                Toast.makeText(this, "Jumlah minimum foto harus dipenuhi!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "Harap isi seluruh data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addFirebaseData(imageByteArray: ArrayList<ByteArray>) {
        var stringFoto: ArrayList<String> = ArrayList()
        for (i in 0 until imageByteArray.size) {
            val imageRef =
                FirebaseStorage.getInstance().getReferenceFromUrl("gs://epilkada.appspot.com/")
                    .child(edNama.text.toString() + "_" + edNIK.text.toString() + "/" + edNama.text + (i + 1).toString())
            imageRef.putBytes(imageByteArray[i]).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    stringFoto.add(it.result.toString())
                    if (stringFoto.size == imageByteArray.size) {
                        val dataBaru = hashMapOf(
                            "nik" to edNIK.text.toString(),
                            "nama" to edNama.text.toString(),
                            "agama" to nilaiAgama,
                            "alamat" to edAlamat.text.toString(),
                            "jk" to jenisKelamin,
                            "kecamatan" to ed_kecamatan.text.toString(),
                            "kel_desa" to ed_keldes.text.toString(),
                            "kewarganegaraan" to negara,
                            "pekerjaan" to kerja,
                            "rt_rw" to ed_rtrw.text.toString(),
                            "status_kawin" to statusKawin,
                            "status_pilbupkot" to false,
                            "status_pilgub" to false,
                            "tempat_lahir" to edTempatLahir.text.toString(),
                            "tgl_lahir" to date!!,
                            "foto" to stringFoto,
                            "kab_kota" to ed_kota.text.toString(),
                            "provinsi" to ed_provinsi.text.toString()
                        )
                        FirebaseFirestore.getInstance().collection("users")
                            .document(edNIK.text.toString())
                            .set(dataBaru).addOnSuccessListener {
                                val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                                dialog.setMessage("Berhasil menyimpan Data.")
                                dialog.setTitle("Proses Berhasil")
                                dialog.setPositiveButton(
                                    "Okay",
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            this@AddPemilih.finish()
                                        }

                                    })
                                val alert = dialog.create()
                                alert.show()

                            }
                    }
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }
    }

    var ambilGambar = false
    var imageData: ArrayList<Bitmap>? = null
    val indexRemoved: ArrayList<Int> = ArrayList()

    //Fungsi untuk melihat data foto yang sudah dipilih admin
    fun lihatFoto() {
        if (ambilGambar) {
            val fragmentManager = this.supportFragmentManager
            val listFoto = fotoPemilih(imageData!!, indexRemoved)
            val fragmentDetail = lihatFotoPemilih()
            val myBundle = Bundle()
            myBundle.putParcelable("BUNDLE_FOTO", listFoto)
            fragmentDetail.arguments = myBundle
            fragmentDetail.show(fragmentManager, "Fragment Detail")
        } else {
            Toast.makeText(this, "Harap ambil data foto dahulu!!!", Toast.LENGTH_SHORT).show()
        }
    }

    //Function untuk membuka galeri admin
    private fun ambilFoto() {
        val intent1 = Intent(Intent.ACTION_PICK)
        intent1.setType("image/*")
        intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent1, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.clipData != null) {
            buatDialog("Mengambil Foto...")
            imageData = ArrayList()
            Log.d("Ndata", data.clipData.toString())

            //Mengambil data foto dari galeri
            doAsync {
                for (i in 0 until data.clipData!!.itemCount) {
                    var inStream = applicationContext.contentResolver.openInputStream(
                        data.clipData!!.getItemAt(i).uri
                    )
                    var bmp = BitmapFactory.decodeStream(inStream)
                    imageData!!.add(rotateImage(bmp, 90f)!!)
                    inStream!!.close()
                }
                uiThread {
                    ambilGambar = true
                    Toast.makeText(
                        applicationContext,
                        "Berhasil mendapatkan foto" + imageData!!.size,
                        Toast.LENGTH_SHORT
                    ).show()
                    tvHitungFoto!!.text = "Dipilih: " + imageData!!.size + " Foto"
                    dialog!!.dismiss()
                }
            }
        }
    }

    private var dialog: ProgressDialog? = null
    fun buatDialog(text: String) {
        dialog = ProgressDialog(this)
        dialog!!.setMessage(text)
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog!!.show()
    }

    private fun rotateImage(bitmap: Bitmap?, i: Float): Bitmap? {
        var matrix = Matrix()
        matrix.postRotate(i)
        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

    }
}