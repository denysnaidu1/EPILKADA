package com.toxic.epilkada

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_jadwal.*
import kotlinx.android.synthetic.main.activity_add_pemilih.*
import kotlinx.android.synthetic.main.activity_edit_jadwal.*
import kotlinx.android.synthetic.main.activity_edit_jadwal.spinnerProvinsiJadwal
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddJadwal : AppCompatActivity() {
    var jenis:List<String>?=null
    var idWilayahLama:ArrayList<String> = ArrayList()
    var idJadwalLama:ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_jadwal)

        FirebaseFirestore.getInstance().collection("jadwal").orderBy("id_jadwal")
            .get().addOnSuccessListener {
                it.forEach { item->
                    idWilayahLama.add(item["id_wilayah"].toString())
                    idJadwalLama.add(item.id)
                }
            }

        jenis= listOf(
            "Jenis Wilayah",
            "Provinsi",
            "Kabupaten/Kota"
        )
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
                dataMap.put("NAMA_PROVINSI",temp)
            }
        }

        spinnerJenisJadwal1.adapter= ArrayAdapter(
            this.applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            jenis!!
        )

        spinnerJenisJadwal1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    spinnerKabKotJadwal1.adapter = ArrayAdapter(
                        this@AddJadwal,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot
                    )
                    spinnerProvinsiJadwal1.adapter = ArrayAdapter<String>(
                        this@AddJadwal,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataProvinsi
                    )
                    spinnerKabKotJadwal1.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsiJadwal1.setBackgroundResource(R.drawable.rectangle_inactive)
                }
                else if (choosen.equals(jenis!![1])) {
                    spinnerProvinsiJadwal1.adapter = ArrayAdapter<String>(
                        this@AddJadwal,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi
                    )
                    spinnerKabKotJadwal1.adapter = ArrayAdapter(
                        this@AddJadwal,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot
                    )
                    spinnerKabKotJadwal1.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsiJadwal1.setBackgroundResource(R.drawable.rectangle)
                    /* spinnerKabKot.isClickable = false
                     spinnerProvinsi.isClickable = true*/
                } else if (choosen.equals(jenis!![2])) {
                    spinnerProvinsiJadwal1.setBackgroundResource(R.drawable.rectangle)
                    spinnerKabKotJadwal1.setBackgroundResource(R.drawable.rectangle)
                    spinnerProvinsiJadwal1.adapter = ArrayAdapter(
                        this@AddJadwal,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi
                    )
                    /* spinnerKabKot.isClickable = true
                     spinnerProvinsi.isClickable=true*/
                }
                Log.d("Choosen", choosen)
            }
        }

        spinnerProvinsiJadwal1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                            spinnerKabKotJadwal1.adapter = ArrayAdapter(
                                this@AddJadwal.applicationContext,
                                android.R.layout.simple_spinner_item,
                                dataKabKot
                            )
                        }
                }
                Log.d("ChoosenProv", choosenProv)
            }
        }

        spinnerKabKotJadwal1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        var date: Date?=null
        edTanggalJadwal1.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            var day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            var month: Int = calendar.get(Calendar.MONTH)
            var year: Int = calendar.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edTanggalJadwal1.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    val dataFormat = SimpleDateFormat("dd/MM/yyyy")
                    date = dataFormat.parse(edTanggalJadwal1.text.toString())
                }, year, month, day
            )
            picker.show()
        }

        btn_addJadwal.setOnClickListener {
            if(choosen==jenis!![0]){
                Toast.makeText(this,"Harap isi data wilayah!", Toast.LENGTH_SHORT).show()
            }else {
                if(choosen==jenis!![1] && choosenProv!=dataProvinsi[0]){
                    if(date!=null){
                        val dialog = ProgressDialog(this)
                        dialog.setMessage("Memperbarui Data...\nProses membutuhkan jaringan internet")
                        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        dialog.show()
                        var cekIdWilayah=false
                        FirebaseFirestore.getInstance().collection("wilayah").whereEqualTo("nama",choosenProv)
                            .get().addOnSuccessListener {
                                for(doc in it){
                                    idWilayahLama.forEach{data->
                                        if(data==doc["id_wilayah"]){
                                            cekIdWilayah=true
                                        }
                                    }
                                    if(cekIdWilayah==false){
                                        var newId=""
                                        var temp=Integer.parseInt(idJadwalLama[idJadwalLama.size-1])+1
                                        newId=temp.toString()
                                        if(newId.length==1){
                                            newId="0"+newId
                                        }
                                        val dataBaru= hashMapOf(
                                            "id_jadwal" to newId,
                                            "id_wilayah" to doc["id_wilayah"],
                                            "tgl_pelaksanaan" to date
                                        )
                                        FirebaseFirestore.getInstance().collection("jadwal").document(newId)
                                            .set(dataBaru).addOnSuccessListener {
                                                dialog.dismiss()
                                                Toast.makeText(this,"Berhasil menambahkan data.",Toast.LENGTH_SHORT).show()
                                            }
                                    }else{
                                        dialog.dismiss()
                                        Toast.makeText(this,"Data wilayah sudah memiliki jadwal!!!",Toast.LENGTH_SHORT).show()
                                    }

                                }
                            }
                    }else{
                        Toast.makeText(this,"Harap isi tanggal kegiatan!!!", Toast.LENGTH_SHORT).show()
                    }
                }
                else if(choosen==jenis!![2] && choosenKabKot!=dataKabKot[0] ){
                    if(date!=null){
                        val dialog = ProgressDialog(this)
                        dialog.setMessage("Memperbarui Data...\nProses membutuhkan jaringan internet")
                        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        dialog.show()
                        var cekIdWilayah=false
                        FirebaseFirestore.getInstance().collection("wilayah").whereEqualTo("nama",choosenProv)
                            .get().addOnSuccessListener {
                                for(doc in it){
                                    FirebaseFirestore.getInstance().collection("wilayah").document(doc.id).collection("kabupaten_kota").whereEqualTo("nama",choosenKabKot)
                                        .get().addOnSuccessListener { document->
                                            for(item in document){
                                                idWilayahLama.forEach{data->
                                                    if(data==item["id_wilayah"]){
                                                        cekIdWilayah=true
                                                    }
                                                }
                                                if(cekIdWilayah==false){
                                                    var newId=""
                                                    var temp=Integer.parseInt(idJadwalLama[idJadwalLama.size-1])+1
                                                    newId=temp.toString()
                                                    if(newId.length==1){
                                                        newId="0"+newId
                                                    }
                                                    val dataBaru= hashMapOf(
                                                        "id_jadwal" to newId,
                                                        "id_wilayah" to item["id_wilayah"],
                                                        "tgl_pelaksanaan" to date
                                                    )
                                                    FirebaseFirestore.getInstance().collection("jadwal").document(newId)
                                                        .set(dataBaru).addOnSuccessListener {
                                                            dialog.dismiss()
                                                            Toast.makeText(this,"Berhasil menambahkan data",Toast.LENGTH_SHORT).show()
                                                        }
                                                }else{
                                                    dialog.dismiss()
                                                    Toast.makeText(this,"Data wilayah sudah memiliki jadwal!!!",Toast.LENGTH_SHORT).show()
                                                }

                                            }
                                        }

                                }

                            }
                    }else{
                        Toast.makeText(this,"Harap isi tanggal kegiatan!!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}