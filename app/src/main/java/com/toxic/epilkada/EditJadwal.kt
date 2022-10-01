package com.toxic.epilkada

import android.app.DatePickerDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_pemilih.*
import kotlinx.android.synthetic.main.activity_add_wilayah.*
import kotlinx.android.synthetic.main.activity_edit_jadwal.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditJadwal : AppCompatActivity() {
    var jenis: List<String>? = null
    var statusWilayah = ""
    var idWilayahLama = ""
    var idJadwalLama = ""
    var idWilayah: ArrayList<String>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_jadwal)
        val intentKabKot = intent.getStringExtra("EXTRA_ID_KABKOT")
        val intentProvinsi = intent.getStringExtra("EXTRA_ID")



        jenis = listOf(
            "Jenis Wilayah",
            "Provinsi",
            "Kabupaten/Kota"
        )

        if (intentKabKot.isNullOrEmpty()) {
            statusWilayah = "provinsi"
            var wilayahLama = intentProvinsi.split(',')
            tv_wilayahLama.text = "Wilayah lama -> "+wilayahLama[1]
            idWilayahLama = wilayahLama[2]
            idJadwalLama = wilayahLama[0]
            FirebaseFirestore.getInstance().collection("jadwal").document(idJadwalLama)
                .get().addOnSuccessListener {
                    val tmp: Timestamp = it["tgl_pelaksanaan"] as Timestamp
                    val tglLama = SimpleDateFormat("dd/MM/yyyy").format(tmp.toDate())
                    Log.d("TglLama", tglLama.toString())
                    edTanggalJadwal.hint = tglLama.toString()
                    FirebaseFirestore.getInstance().collection("jadwal").orderBy("id_jadwal")
                        .get().addOnSuccessListener {
                            it.forEach { item ->
                                if (item["id_wilayah"].toString() != idWilayahLama) {
                                    idWilayah!!.add(item["id_wilayah"].toString())
                                }
                            }
                        }
                }

        } else {
            statusWilayah = "kabkot"
            var wilayahLama = intentKabKot.split(',')
            tv_wilayahLama.text = "Wilayah lama -> "+wilayahLama[1]
            idWilayahLama = wilayahLama[2]
            idJadwalLama = wilayahLama[0]
            FirebaseFirestore.getInstance().collection("jadwal").document(idJadwalLama)
                .get().addOnSuccessListener {
                    val tmp: Timestamp = it["tgl_pelaksanaan"] as Timestamp
                    val tglLama = SimpleDateFormat("dd/MM/yyyy").format(tmp.toDate())
                    Log.d("TglLama", tglLama.toString())
                    edTanggalJadwal.hint = tglLama.toString()
                    FirebaseFirestore.getInstance().collection("jadwal").orderBy("id_jadwal")
                        .get().addOnSuccessListener {
                            it.forEach { item ->
                                if (item["id_wilayah"].toString() != idWilayahLama) {
                                    idWilayah!!.add(item["id_wilayah"].toString())
                                }
                            }
                        }
                }
        }

        Log.d("StatusWil", statusWilayah)

        var choosen = ""
        var choosenProv = ""
        var choosenKabKot = ""
        var dataKabKot: ArrayList<String> = ArrayList()
        var dataMap: HashMap<String, String> = HashMap<String, String>()
        var dataProvinsi: ArrayList<String> = ArrayList()
        dataProvinsi.add("Provinsi")
        dataKabKot.add("Kota/kabupaten")

        val dbProvinsi = FirebaseFirestore.getInstance().collection("wilayah").orderBy("nama")
        dbProvinsi.get().addOnSuccessListener {
            for (document in it) {
                var temp = document.data["nama"].toString()
                dataProvinsi.add(document.data["nama"].toString())
                dataMap.put("ID_PROVINSI", document.id)
                dataMap.put("NAMA_PROVINSI", temp)
            }
        }

        spinnerJenisJadwal.adapter = ArrayAdapter(
            this.applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            jenis!!
        )

        spinnerJenisJadwal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosen = parent!!.getItemAtPosition(position).toString()
                if (choosen.equals(jenis!![0])) {
                    spinnerKabKotJadwal.adapter = ArrayAdapter(
                        this@EditJadwal.applicationContext!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot
                    )
                    spinnerProvinsiJadwal.adapter = ArrayAdapter<String>(
                        this@EditJadwal.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataProvinsi
                    )
                    spinnerKabKotJadwal.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsiJadwal.setBackgroundResource(R.drawable.rectangle_inactive)
                } else if (choosen.equals(jenis!![1])) {
                    spinnerProvinsiJadwal.adapter = ArrayAdapter<String>(
                        this@EditJadwal.applicationContext,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi
                    )
                    spinnerKabKotJadwal.adapter = ArrayAdapter(
                        this@EditJadwal.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot
                    )
                    spinnerKabKotJadwal.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsiJadwal.setBackgroundResource(R.drawable.rectangle)
                    /* spinnerKabKot.isClickable = false
                     spinnerProvinsi.isClickable = true*/
                } else if (choosen.equals(jenis!![2])) {
                    spinnerProvinsiJadwal.setBackgroundResource(R.drawable.rectangle)
                    spinnerKabKotJadwal.setBackgroundResource(R.drawable.rectangle)
                    spinnerProvinsiJadwal.adapter = ArrayAdapter(
                        this@EditJadwal.applicationContext,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi
                    )
                    /* spinnerKabKot.isClickable = true
                     spinnerProvinsi.isClickable=true*/
                }
                Log.d("Choosen", choosen)
            }
        }

        spinnerProvinsiJadwal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosenProv = parent!!.getItemAtPosition(position).toString()
                if (choosen != "Pemilihan Gubernur" && choosen != "Jenis Pemilihan" && choosenProv != dataProvinsi[0]) {
                    val dbKabKot = FirebaseFirestore.getInstance().collection("wilayah")
                        .document(dataMap["ID_PROVINSI"]!!).collection("kabupaten_kota")
                        .orderBy("nama")
                    dbKabKot.get()
                        .addOnSuccessListener {
                            for (document in it) {
                                dataKabKot.add(document.data["nama"].toString())
                            }
                            spinnerKabKotJadwal.adapter = ArrayAdapter(
                                this@EditJadwal.applicationContext,
                                android.R.layout.simple_spinner_item,
                                dataKabKot
                            )
                        }
                }
                Log.d("ChoosenProv", choosenProv)
            }
        }

        spinnerKabKotJadwal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosenKabKot = parent!!.getItemAtPosition(position).toString()
                Log.d("ChoosenKabkot", choosenKabKot)
            }
        }

        var date: Date? = null
        edTanggalJadwal.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            var day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            var month: Int = calendar.get(Calendar.MONTH)
            var year: Int = calendar.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edTanggalJadwal.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    val dataFormat = SimpleDateFormat("dd/MM/yyyy")
                    date = dataFormat.parse(edTanggalJadwal.text.toString())
                }, year, month, day
            )
            picker.show()
        }

        btn_updateHasil.setOnClickListener {
            if (choosen == jenis!![0] || date == null) {
                Toast.makeText(this, "Harap isi seluruh data!!!", Toast.LENGTH_SHORT).show()
            } else {
                if (choosen == jenis!![1] && choosenProv != dataProvinsi[0]) {
                    var cekWil = false
                    val dialog = ProgressDialog(this)
                    dialog.setMessage("Memperbarui Data...\nProses membutuhkan jaringan internet")
                    dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    dialog.show()
                    FirebaseFirestore.getInstance().collection("wilayah")
                        .whereEqualTo("nama", choosenProv).get()
                        .addOnSuccessListener {
                            it.forEach { item ->
                                for (data in idWilayah!!) {
                                    if (item["id_wilayah"].toString() == data) {
                                        cekWil = true
                                    }
                                }
                                if (cekWil == false) {
                                    val dataBaru: HashMap<String, Any> = hashMapOf(
                                        "id_jadwal" to idJadwalLama,
                                        "id_wilayah" to item["id_wilayah"]!!,
                                        "tgl_pelaksanaan" to date!!
                                    )
                                    FirebaseFirestore.getInstance().collection("jadwal")
                                        .document(idJadwalLama)
                                        .update(dataBaru).addOnSuccessListener {
                                            dialog.dismiss()
                                            Toast.makeText(
                                                this,
                                                "Berhasil memperbarui data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            this.finish()
                                        }
                                } else {
                                    dialog.dismiss()
                                    Toast.makeText(
                                        this,
                                        "Provinsi ini sudah memiliki jadwal!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                } else if (choosen == jenis!![2] && choosenKabKot != dataKabKot[0]) {
                    var cekWil = false
                    val dialog = ProgressDialog(this)
                    dialog.setMessage("Memperbarui Data...\nProses membutuhkan jaringan internet")
                    dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    dialog.show()
                    FirebaseFirestore.getInstance().collection("wilayah")
                        .whereEqualTo("nama", choosenProv).get()
                        .addOnSuccessListener {
                            it.forEach { item ->
                                FirebaseFirestore.getInstance().collection("wilayah")
                                    .document(item.id)
                                    .collection("kabupaten_kota")
                                    .whereEqualTo("nama", choosenKabKot).get()
                                    .addOnSuccessListener { docs ->
                                        docs.forEach { doc ->
                                            for (data in idWilayah!!) {
                                                if (doc["id_wilayah"].toString() == data) {
                                                    cekWil = true
                                                }
                                            }
                                            if (cekWil == false) {
                                                val dataBaru: HashMap<String, Any> = hashMapOf(
                                                    "id_jadwal" to idJadwalLama,
                                                    "id_wilayah" to doc["id_wilayah"]!!,
                                                    "tgl_pelaksanaan" to date!!
                                                )
                                                FirebaseFirestore.getInstance().collection("jadwal")
                                                    .document(idJadwalLama)
                                                    .update(dataBaru).addOnSuccessListener {
                                                        dialog.dismiss()
                                                        Toast.makeText(
                                                            this,
                                                            "Berhasil memperbarui data",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        this.finish()
                                                    }
                                            } else {
                                                dialog.dismiss()
                                                Toast.makeText(
                                                    this,
                                                    "Provinsi ini sudah memiliki jadwal!!!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                            }
                        }
                }
            }
        }

    }
}