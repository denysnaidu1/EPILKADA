package com.toxic.epilkada

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_jenis_pemilihan.*

class JenisPemilihan : AppCompatActivity() {
    var datapemilih: pemilih? = null
    var intentAktivitas: Intent? = null
    var statusBupKot = false
    var statusGubernur = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jenis_pemilihan)
        intentAktivitas = Intent(this@JenisPemilihan, Pemilihan::class.java)
        datapemilih = this.intent.getParcelableExtra("dataExtra")
        Log.d("NIK", datapemilih!!.nik)
        FirebaseFirestore.getInstance().collection("users").document(datapemilih!!.nik)
            .get().addOnSuccessListener {
                statusBupKot = it["status_pilbupkot"] as Boolean
                statusGubernur = it["status_pilgub"] as Boolean
                if (!statusBupKot) {
                    btn_pilbup.isEnabled = true
                    btn_pilbup.setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
                if (!statusGubernur) {
                    btn_pilgub.isEnabled = true
                    btn_pilgub.setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
                if (statusBupKot && statusGubernur) {
                    showDialog("Anda sudah melakukan seluruh pemilihan.", "Tidak Dapat Memilih", 0)
                }
            }
    }

    private fun showDialog(text: String, title: String, flag: Int) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setMessage(text)
        dialog.setTitle(title)
        dialog.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                intentAktivitas = Intent(this@JenisPemilihan, MainActivity::class.java)
                intentAktivitas!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentAktivitas)
                this@JenisPemilihan.finish()
            }
        })
        if (flag == 1) {
            dialog.setNegativeButton("Tidak", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }
            })
        }
        val alert = dialog.create()
        alert.show()
    }

    override fun onBackPressed() {
        showDialog(
            "Jika Anda kembali, Anda harus mengulangi tahapan identifikasi dari awal. Apakah Anda yakin?",
            "Batal Memilih",
            1
        )
    }

    fun goPilGub(view: View) {
        Log.d("Berhasil", "Buka Aktivitas Pilgub")
        datapemilih!!.jenisPilihan = "pilgub"
        intentAktivitas!!.putExtra("dataExtra", datapemilih)
        startActivity(intentAktivitas)
    }

    fun goPilBup(view: View) {
        Log.d("Berhasil", "Buka Aktivitas Pilbupkot")
        datapemilih!!.jenisPilihan = "pilbupkot"
        intentAktivitas!!.putExtra("dataExtra", datapemilih)
        startActivity(intentAktivitas)
    }
}