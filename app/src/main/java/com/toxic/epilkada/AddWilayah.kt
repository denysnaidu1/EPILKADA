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
import kotlinx.android.synthetic.main.activity_add_pemilihan.*
import kotlinx.android.synthetic.main.activity_add_pemilihan.spinnerJenis
import kotlinx.android.synthetic.main.activity_add_wilayah.*

class AddWilayah : AppCompatActivity() {
    private var jenis: List<String>? = null
    var choosen = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wilayah)
        jenis = listOf(
            "Jenis Wilayah",
            "Provinsi",
            "Kabupaten/Kota"
        )
        spinnerJenisWilayah.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            jenis!!
        )

        spinnerJenisWilayah.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 2) {
                    edNama_Kabkot.isEnabled = true
                    edNama_Provinsi.isEnabled = true
                    choosen = parent!!.getItemAtPosition(position).toString()
                } else if (position == 1) {
                    edNama_Provinsi.isEnabled = true
                    edNama_Kabkot.isEnabled = false
                    choosen = parent!!.getItemAtPosition(position).toString()
                } else {
                    edNama_Kabkot.isEnabled = false
                    edNama_Provinsi.isEnabled = false
                }
            }
        }
    }

    fun addWilayah(view: View) {
        if (choosen == jenis!![1]) {
            val dialog = ProgressDialog(this)
            dialog.setMessage("Menambahkan Data...\nProses membutuhkan jaringan internet")
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            dialog.show()
            if (edNama_Provinsi.text.isNotBlank() && edjumlah_Penduduk.text.isNotBlank() && edKodeWilayah.text.isNotBlank()) {
                FirebaseFirestore.getInstance().collection("wilayah")
                    .whereEqualTo("nama", edNama_Provinsi.text.toString())
                    .get().addOnSuccessListener {
                        if (it.size() < 1) {
                            val dataBaru = hashMapOf(
                                "id_wilayah" to edKodeWilayah.text.toString(),
                                "jlh_penduduk" to edjumlah_Penduduk.text.toString().toInt(),
                                "nama" to edNama_Provinsi.text.toString()
                            )
                            FirebaseFirestore.getInstance().collection("wilayah")
                                .document(edKodeWilayah.text.toString())
                                .set(dataBaru).addOnSuccessListener {
                                    dialog.dismiss()
                                    this.finish()
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Data dengan kode wilayah ini sudah ada!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Harap isi seluruh form yang ada!", Toast.LENGTH_SHORT).show()
            }
        } else if (choosen == jenis!![2]) {
            if (edNama_Provinsi.text.isNotBlank() && edNama_Kabkot.text.isNotBlank() && edjumlah_Penduduk.text.isNotBlank() && edKodeWilayah.text.isNotBlank()) {
                    val dialog = ProgressDialog(this)
                    dialog.setMessage("Menambahkan Data...\nProses membutuhkan jaringan internet")
                    dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    dialog.show()
                    FirebaseFirestore.getInstance().collection("wilayah")
                        .whereEqualTo("nama", edNama_Provinsi.text.toString())
                        .get().addOnSuccessListener {
                            if (it.size() == 1) {
                                for (doc in it) {
                                    val dataBaru = hashMapOf(
                                        "id_wilayah" to doc.id+edKodeWilayah.text.toString(),
                                        "jlh_penduduk" to edjumlah_Penduduk.text.toString().toInt(),
                                        "nama" to edNama_Kabkot.text.toString()
                                    )
                                    FirebaseFirestore.getInstance().collection("wilayah")
                                        .document(doc.id).collection("kabupaten_kota")
                                        .document(edKodeWilayah.text.toString()).set(dataBaru)
                                        .addOnSuccessListener {
                                            dialog.dismiss()
                                            this.finish()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Gagal. Data provinsi tidak ditemukan!",
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            }
                        }


            } else {
                Toast.makeText(this, "Harap isi seluruh form!!!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Harap pilih jenis pemilihan", Toast.LENGTH_SHORT).show()
        }
    }
}