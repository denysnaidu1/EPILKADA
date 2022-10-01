package com.toxic.epilkada

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_wilayah.*
import kotlinx.android.synthetic.main.activity_add_wilayah.edNama_Kabkot
import kotlinx.android.synthetic.main.activity_add_wilayah.edNama_Provinsi
import kotlinx.android.synthetic.main.activity_add_wilayah.spinnerJenisWilayah
import kotlinx.android.synthetic.main.activity_edit_wilayah.*

class EditWilayah : AppCompatActivity() {
    var idKabKot=""
    var defaultId=""
    var dataExtra:List<String>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_wilayah)
        loadData()
    }

    private fun loadData() {
        defaultId=intent.getStringExtra("EXTRA_ID")
        dataExtra=defaultId.split(",")
        if(dataExtra!![1].equals("provinsi")){
            FirebaseFirestore.getInstance().collection("wilayah").document(dataExtra!![0])
                .get().addOnSuccessListener {
                    edNamaProv.hint=it["nama"].toString()
                    edKode.hint=it.id
                    edjumlahPenduduk.hint=it["jlh_penduduk"].toString()
                }
            edNamaKabkot.isEnabled=false
            edNamaKabkot.isClickable=false
            edNamaKabkot.hint="-"
        }
        else if(dataExtra!![1].equals("kabkot")){
            idKabKot=dataExtra!![0].substring(2)
            FirebaseFirestore.getInstance().collection("wilayah").document("12")
                .get().addOnSuccessListener {item->
                    FirebaseFirestore.getInstance().collection("wilayah").document("12")
                        .collection("kabupaten_kota").document(idKabKot)
                        .get().addOnSuccessListener {
                            edNamaProv.hint=item["nama"].toString()
                            edNamaKabkot.hint=it["nama"].toString()
                            edKode.hint=it["id_wilayah"].toString()
                            edjumlahPenduduk.hint=it["jlh_penduduk"].toString()
                        }
                }

            edNamaProv.isEnabled=false
            edNamaProv.isClickable=false
        }
    }

    fun saveWilayah(view: View) {
        if(dataExtra!![1].equals("provinsi")){
            if(edNamaProv.text.isNotBlank() && edjumlahPenduduk.text.isNotBlank()){
                val dialog = ProgressDialog(this)
                dialog.setMessage("Memperbarui Data...\nProses membutuhkan jaringan internet")
                dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                dialog.show()
                val dataBaru = hashMapOf(
                    "id_wilayah" to edKode.hint,
                    "jlh_penduduk" to edjumlahPenduduk.text.toString().toInt(),
                    "nama" to edNamaProv.text.toString()
                )
                FirebaseFirestore.getInstance().collection("wilayah").document(dataExtra!![0])
                    .update(dataBaru).addOnSuccessListener {
                        dialog.dismiss()
                        Toast.makeText(this,"Berhasil edit data",Toast.LENGTH_SHORT).show()
                        this.finish()
                    }
            }else{
                Toast.makeText(this,"Harap isi seluruh form data!",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            if(edjumlahPenduduk.text.isNotBlank() && edNamaKabkot.text.isNotBlank()){
                val dialog = ProgressDialog(this)
                dialog.setMessage("Memperbarui Data...\nProses membutuhkan jaringan internet")
                dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                dialog.show()
                val dataBaru :HashMap<String,Any> = hashMapOf(
                    "id_wilayah" to dataExtra!![0],
                    "jlh_penduduk" to edjumlahPenduduk.text.toString().toInt(),
                    "nama" to edNamaKabkot.text.toString()
                )
                FirebaseFirestore.getInstance().collection("wilayah").document("12").collection("kabupaten_kota")
                    .document(idKabKot)
                    .update(dataBaru).addOnSuccessListener {
                        dialog.dismiss()
                        Toast.makeText(this,"Berhasil edit data",Toast.LENGTH_SHORT).show()
                        this.finish()
                    }
            }else{
                Toast.makeText(this,"Harap isi seluruh form data!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}