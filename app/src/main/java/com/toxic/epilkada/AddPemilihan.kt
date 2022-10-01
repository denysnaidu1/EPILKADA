package com.toxic.epilkada

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_add_paslon.*
import kotlinx.android.synthetic.main.activity_add_pemilihan.*
import kotlinx.android.synthetic.main.activity_add_pemilihan.spinnerJenis
import kotlinx.android.synthetic.main.activity_add_pemilihan.spinnerKabKot
import kotlinx.android.synthetic.main.activity_add_pemilihan.spinnerProvinsi
import kotlinx.android.synthetic.main.activity_admin_paslon.*
import kotlinx.android.synthetic.main.fragment_detail_pemilihan.*

class AddPemilihan : AppCompatActivity() {

    private var jenis: List<String>? = null
    var choosen = ""
    var choosenProv = ""
    var choosenKabKot = ""
    var dataKabKot: ArrayList<String>? = null
    var dataMap: HashMap<String, String>? = null
    var dataProvinsi: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pemilihan)
        jenis = listOf(
            "Jenis pemilihan",
            "Pemilihan Gubernur",
            "Pemilihan Bupati/Wali Kota"
        )

        dataKabKot = ArrayList()
        dataMap = HashMap<String, String>()
        dataProvinsi = ArrayList()
        dataProvinsi!!.add("Provinsi")
        dataKabKot!!.add("Kota/kabupaten")

        val dbProvinsi = FirebaseFirestore.getInstance().collection("wilayah").orderBy("nama")
        dbProvinsi.get().addOnSuccessListener {
            for (document in it) {
                var temp = document.data["nama"].toString()
                dataProvinsi!!.add(document.data["nama"].toString())
                dataMap!!.put("ID_PROVINSI", document.id)
                dataMap!!.put("NAMA_PROVINSI", temp)
            }
        }
        spinnerJenis.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            jenis!!
        )
        spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
        spinnerProvinsi.setBackgroundResource(R.drawable.rectangle_inactive)

        spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    spinnerKabKot.adapter = ArrayAdapter(
                        this@AddPemilihan.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot!!
                    )
                    spinnerProvinsi.adapter = ArrayAdapter<String>(
                        this@AddPemilihan.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataProvinsi!!
                    )
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle_inactive)
                } else if (choosen.equals(jenis!![1])) {
                    spinnerProvinsi.adapter = ArrayAdapter<String>(
                        this@AddPemilihan.applicationContext,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi!!
                    )
                    spinnerKabKot.adapter = ArrayAdapter(
                        this@AddPemilihan.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot!!
                    )
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle)
                } else if (choosen.equals(jenis!![2])) {
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle)
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle)
                    spinnerProvinsi.adapter = ArrayAdapter(
                        this@AddPemilihan.applicationContext,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi!!
                    )

                }
            }
        }

        spinnerProvinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosenProv = parent!!.getItemAtPosition(position).toString()
                if (choosen != "Pemilihan Gubernur" && choosen != "Jenis Pemilihan" && choosenProv != dataProvinsi!![0]) {
                    val dbKabKot = FirebaseFirestore.getInstance().collection("wilayah")
                        .document(dataMap!!["ID_PROVINSI"]!!).collection("kabupaten_kota")
                        .orderBy("nama")
                    dbKabKot.get()
                        .addOnSuccessListener {
                            for (document in it) {
                                dataKabKot!!.add(document.data["nama"].toString())
                            }
                            spinnerKabKot.adapter = ArrayAdapter(
                                this@AddPemilihan.applicationContext,
                                android.R.layout.simple_spinner_item,
                                dataKabKot!!
                            )
                        }
                }
            }
        }

        spinnerKabKot.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosenKabKot = parent!!.getItemAtPosition(position).toString()
            }
        }

        edPaslon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                status = false
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                status = false
            }

        })
    }

    //Function untuk cek IDPaslon sudah terdaftar atau belum
    var status = false
    fun checkId(view: View) {
        if (edPaslon.text.isNotBlank()) {
            FirebaseFirestore.getInstance().collection("paslon").document(edPaslon.text.toString())
                .get().addOnSuccessListener {
                    if (it.exists()) {
                        Toast.makeText(this,"Id sudah terdaftar!!!",Toast.LENGTH_SHORT).show()
                        status=false
                    } else {
                        status=true
                        Toast.makeText(this, "Id dapat diinput.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Harap isi ID Paslon!", Toast.LENGTH_SHORT).show()
        }
    }

    //Function untuk menambahkan data ke Firebase
    var newId = ""
    fun tambahData(view: View) {
        val dialog = ProgressDialog(this)
        dialog.setMessage("Menambahkan Data...\nProses membutuhkan jaringan internet")
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog.show()
        if (status != true) {
            dialog.dismiss()
            Toast.makeText(this, "Harap tekan tombol check id Paslon!", Toast.LENGTH_SHORT).show()
        } else {
            if (choosen == jenis!![0]) {
                dialog.dismiss()
                Toast.makeText(this, "Harap isi data wilayah!", Toast.LENGTH_SHORT).show()
            } else {
                var jlhDukungan = 0
                if (edJlhDukungan.text.isNotEmpty()) {
                    jlhDukungan = edJlhDukungan.text.toString().toInt()
                    if (jlhDukungan >= 20 && jlhDukungan <= 100) {
                        if (choosen == jenis!![1]) {
                            if (choosenProv != dataProvinsi!![0]) {
                                var i = 0
                                var tempJlh = 0
                                FirebaseFirestore.getInstance().collection("pemilihan").orderBy(
                                    "id_pemilihan",
                                    Query.Direction.DESCENDING
                                ).get()
                                    .addOnSuccessListener {
                                        for (doc in it) {
                                            if (i == 0) {
                                                newId = doc.id
                                            }
                                            i++
                                            if (doc.data["nama_wilayah"].toString() == choosenProv.toLowerCase()) {
                                                jlhDukungan += doc.data["jlh_dukungan"].toString()
                                                    .toInt()
                                                Log.d("JlhDukungan",jlhDukungan.toString())
                                            }
                                        }
                                        newId = (newId.toInt() + 1).toString()
                                        if (newId.length == 2) {
                                            newId = "0" + newId
                                        } else if (newId.length == 1) {
                                            newId = "00" + newId
                                        }
                                        Log.d("JlhDukunganFinal",jlhDukungan.toString())
                                        if (jlhDukungan <= 100) {
                                            val dataBaru = hashMapOf(
                                                "id_pemilihan" to newId,
                                                "id_paslon" to edPaslon.text.toString(),
                                                "nama_wilayah" to choosenProv.toLowerCase(),
                                                "jlh_suara" to 0,
                                                "jlh_dukungan" to edJlhDukungan.text.toString().toInt()
                                            )
                                            FirebaseFirestore.getInstance().collection("pemilihan")
                                                .document(newId)
                                                .set(dataBaru).addOnSuccessListener {
                                                    dialog.dismiss()
                                                    this.finish()
                                                }
                                        }else{
                                            Toast.makeText(this,"Proses Gagal!. Nilai dukungan pada wilayah melebihi 100%",Toast.LENGTH_SHORT).show()
                                            dialog.dismiss()
                                        }
                                    }

                            } else {
                                dialog.dismiss()
                                Toast.makeText(this, "Harap isi data provinsi!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else if (choosen == jenis!![2]) {
                            if (choosenProv != dataProvinsi!![0] && choosenKabKot != dataKabKot!![0]) {
                                var i = 0
                                FirebaseFirestore.getInstance().collection("pemilihan").orderBy(
                                    "id_pemilihan",
                                    Query.Direction.DESCENDING
                                ).get()
                                    .addOnSuccessListener {
                                        for (doc in it) {
                                            if (i == 0) {
                                                newId = doc.id
                                            }
                                            i++
                                            if (doc.data["nama_wilayah"].toString() == choosenKabKot.toLowerCase()) {
                                                jlhDukungan += doc.data["jlh_dukungan"].toString()
                                                    .toInt()
                                                Log.d("JlhDukungan",jlhDukungan.toString())
                                            }
                                        }
                                        newId = (newId.toInt() + 1).toString()
                                        if (newId.length == 2) {
                                            newId = "0" + newId
                                        } else if (newId.length == 1) {
                                            newId = "00" + newId
                                        }
                                        if (jlhDukungan <= 100) {
                                            val dataBaru = hashMapOf(
                                                "id_pemilihan" to newId,
                                                "id_paslon" to edPaslon.text.toString(),
                                                "nama_wilayah" to choosenKabKot.toLowerCase(),
                                                "jlh_suara" to 0,
                                                "jlh_dukungan" to edJlhDukungan.text.toString().toInt()
                                            )
                                            FirebaseFirestore.getInstance().collection("pemilihan")
                                                .document(newId)
                                                .set(dataBaru).addOnSuccessListener {
                                                    dialog.dismiss()
                                                    this.finish()
                                                }
                                        }else{
                                            Toast.makeText(this,"Proses Gagal!. Nilai dukungan pada wilayah melebihi 100%",Toast.LENGTH_SHORT).show()
                                            dialog.dismiss()
                                        }
                                    }
                            } else {
                                dialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Harap isi data provinsi dan kabupaten/kota!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Nilai dukungan tidak boleh kurang dari 20 dan kurang dari 100",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                } else {
                    dialog.dismiss()
                    Toast.makeText(this, "Harap isi jumlah dukungan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}