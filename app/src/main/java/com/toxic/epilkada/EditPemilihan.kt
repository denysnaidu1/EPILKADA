package com.toxic.epilkada

import android.app.ProgressDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_add_pemilihan.*
import kotlinx.android.synthetic.main.activity_add_pemilihan.edPaslon
import kotlinx.android.synthetic.main.activity_add_pemilihan.spinnerJenis
import kotlinx.android.synthetic.main.activity_add_pemilihan.spinnerKabKot
import kotlinx.android.synthetic.main.activity_add_pemilihan.spinnerProvinsi
import kotlinx.android.synthetic.main.activity_edit_pemilihan.*

class EditPemilihan : AppCompatActivity() {
    private var jenis:List<String>?=null
    var choosen = ""
    var choosenProv = ""
    var choosenKabKot = ""
    var dataKabKot: ArrayList<String>?=null
    var dataMap: HashMap<String, String>?=null
    var dataProvinsi: ArrayList<String>?=null
    var defaultId=""
    var defaultIdPaslon=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pemilihan)

        defaultId=intent.getStringExtra("EXTRA_ID")!!
        FirebaseFirestore.getInstance().collection("pemilihan").document(defaultId)
            .get().addOnSuccessListener {
                edDukungan.hint="jumlah dukungan lama -> "+it["jlh_dukungan"].toString()
                tvWilayahLama.text="Wilayah Lama -> "+it["nama_wilayah"]
                edPaslon.hint="id lama -> "+it["id_paslon"]
                defaultIdPaslon=it["id_paslon"].toString()
            }
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
                dataMap!!.put("NAMA_PROVINSI",temp)
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
                if(choosen.equals(jenis!![0])){
                    spinnerKabKot.adapter = ArrayAdapter(
                        this@EditPemilihan.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot!!
                    )
                    spinnerProvinsi.adapter = ArrayAdapter<String>(
                        this@EditPemilihan.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataProvinsi!!
                    )
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle_inactive)
                }
                else if (choosen.equals(jenis!![1])) {
                    spinnerProvinsi.adapter = ArrayAdapter<String>(
                        this@EditPemilihan.applicationContext,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi!!
                    )
                    spinnerKabKot.adapter = ArrayAdapter(
                        this@EditPemilihan.applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot!!
                    )
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle)
                } else if (choosen.equals(jenis!![2])) {
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle)
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle)
                    spinnerProvinsi.adapter = ArrayAdapter(
                        this@EditPemilihan.applicationContext,
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
                                this@EditPemilihan.applicationContext,
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


        edPaslon.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                status=false
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                status=false
            }

        })
    }

    //Function untuk cek IDPaslon sudah terdaftar atau belum
    var status=false
    fun checkId(view: View) {
        if(edPaslon.text.isNotBlank()){
            FirebaseFirestore.getInstance().collection("paslon").document(edPaslon.text.toString())
                .get().addOnSuccessListener {
                    if(it.exists()){
                        val dialog1: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@EditPemilihan)
                        dialog1.setMessage("Apakah data sudah benar?\nKetua      : "+it["nama1"]+"\nWakil      : "+it["nama2"]+"\nNo.Urut  : "+it["nomor"].toString())
                        dialog1.setTitle("Data Paslon")
                        dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                status=true
                                FirebaseFirestore.getInstance().collection("pemilihan").whereEqualTo("id_paslon",edPaslon.text.toString())
                                    .get().addOnSuccessListener {
                                        if(it.size()>0){
                                            for(doc in it){
                                                Log.d("Ceeeek",doc["id_paslon"].toString()+"----"+defaultIdPaslon)
                                                if(doc["id_paslon"].toString()!=defaultIdPaslon){
                                                    status=false
                                                    Toast.makeText(this@EditPemilihan,"Id Paslon ini sudah terdaftar di pemilihan lain!",
                                                        Toast.LENGTH_LONG).show()
                                                }
                                                else{
                                                    status=true
                                                }
                                            }
                                        }
                                        else{
                                            status=true
                                        }
                                    }
                            }
                        })
                        dialog1.setNegativeButton("Tidak",object: DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                status=false
                            }

                        })
                        val alert1 = dialog1.create()
                        alert1.show()
                    }
                    else{
                        Toast.makeText(this,"Id tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this,"Harap isi ID Paslon!", Toast.LENGTH_SHORT).show()
        }
    }


    //Function untuk mengupdate data ke Firebase
    fun tambahData(view: View) {
        val dialog = ProgressDialog(this)
        dialog.setMessage("Menambahkan Data...\nProses membutuhkan jaringan internet")
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog.show()
        if(status!=true){
            dialog.dismiss()
            Toast.makeText(this,"Harap tekan tombol check id Paslon!",Toast.LENGTH_SHORT).show()
        }else{
            if(choosen==jenis!![0]){
                dialog.dismiss()
                Toast.makeText(this,"Harap isi data wilayah!",Toast.LENGTH_SHORT).show()
            }else {
                var jlhDukungan=0
                if(edDukungan.text.isNotEmpty()){
                    jlhDukungan=edDukungan.text.toString().toInt()
                    if(jlhDukungan>=20 && jlhDukungan<=100){
                        if (choosen == jenis!![1]) {
                            if (choosenProv != dataProvinsi!![0]) {
                                FirebaseFirestore.getInstance().collection("pemilihan").orderBy("id_pemilihan",
                                    Query.Direction.DESCENDING).get()
                                    .addOnSuccessListener {
                                        for(doc in it){
                                            if (doc.data["nama_wilayah"].toString() == choosenProv.toLowerCase() && doc.id!=defaultId) {
                                                jlhDukungan += doc.data["jlh_dukungan"].toString()
                                                    .toInt()
                                                Log.d("JlhDukungan",jlhDukungan.toString())
                                            }
                                        }
                                        Log.d("jlhDukungan",jlhDukungan.toString())
                                        if (jlhDukungan <= 100) {
                                            val dataBaru = hashMapOf(
                                                "id_pemilihan" to defaultId,
                                                "id_paslon" to edPaslon.text.toString(),
                                                "nama_wilayah" to choosenProv.toLowerCase(),
                                                "jlh_suara" to 0,
                                                "jlh_dukungan" to edDukungan.text.toString().toInt()
                                            )
                                            FirebaseFirestore.getInstance().collection("pemilihan")
                                                .document(defaultId)
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
                                FirebaseFirestore.getInstance().collection("pemilihan").orderBy("id_pemilihan",
                                    Query.Direction.DESCENDING).get()
                                    .addOnSuccessListener {
                                        for(doc in it){
                                            if (doc.data["nama_wilayah"].toString() == choosenKabKot.toLowerCase() && doc.id!=defaultId) {
                                                jlhDukungan += doc.data["jlh_dukungan"].toString()
                                                    .toInt()
                                                Log.d("JlhDukungan",jlhDukungan.toString())
                                            }
                                        }

                                        Log.d("jlhDukungan",jlhDukungan.toString())
                                        if (jlhDukungan <= 100) {
                                            val dataBaru = hashMapOf(
                                                "id_pemilihan" to defaultId,
                                                "id_paslon" to edPaslon.text.toString(),
                                                "nama_wilayah" to choosenKabKot.toLowerCase(),
                                                "jlh_suara" to 0,
                                                "jlh_dukungan" to edDukungan.text.toString().toInt()
                                            )
                                            FirebaseFirestore.getInstance().collection("pemilihan")
                                                .document(defaultId)
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
                    }else{
                        Toast.makeText(this,"Jumlah dukungan paslon harus lebih dari 20% atau kurang dari 100%!!!",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"Harap isi jumlah dukungan paslon!!!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}