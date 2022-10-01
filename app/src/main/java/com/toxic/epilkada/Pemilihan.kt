package com.toxic.epilkada

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_pemilihan.*

class Pemilihan : AppCompatActivity() {

    private var dataPemilih: pemilih? = null
    private var status = ""
    private var adapter: FirestoreRecyclerAdapter<DataPemilihan, ItemViewHolder>? = null
    private var options: FirestoreRecyclerOptions<DataPemilihan>? = null
    private var query: Query? = null
    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pemilihan)
        dialog = ProgressDialog(this@Pemilihan)
        dialog!!.setMessage("Mempersiapkan Data...\nProses membutuhkan jaringan internet")
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog!!.show()
        dataPemilih = this@Pemilihan.intent.getParcelableExtra("dataExtra")
        var wilayah = ""
        if (dataPemilih!!.jenisPilihan.equals("pilgub")) {
            status = "pilgub"
            wilayah = dataPemilih!!.provinsi.toLowerCase()
        } else if (dataPemilih!!.jenisPilihan.equals("pilbupkot")) {
            status = "pilbupkot"
            wilayah = dataPemilih!!.kab_kota.toLowerCase()
        }

        query = FirebaseFirestore.getInstance().collection("pemilihan")
            .whereEqualTo("nama_wilayah", wilayah)
        options = FirestoreRecyclerOptions.Builder<DataPemilihan>()
            .setQuery(query!!, DataPemilihan::class.java).build()
        adapter = object : FirestoreRecyclerAdapter<DataPemilihan, ItemViewHolder>(options!!) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
                return ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ItemViewHolder,
                position: Int,
                model: DataPemilihan
            ) {
                val docRefPaslon =
                    FirebaseFirestore.getInstance().collection("paslon").document(model.id_paslon)
                docRefPaslon.get()
                    .addOnSuccessListener {
                        dialog!!.dismiss()
                        if (it.exists()) {
                            val dataPaslon: Paslon = Paslon(
                                it["nama1"].toString(),
                                it["nama2"].toString(),
                                Integer.parseInt(it["nomor"].toString()),
                                it["nik1"].toString(),
                                it["nik2"].toString(),
                                it["partai"].toString(),
                                it["foto"].toString()
                            )
                            holder.populateFoto(dataPaslon.foto, this@Pemilihan.applicationContext)
                            holder.populateNama(dataPaslon.nama1, dataPaslon.nama2)
                            if (status == "pilgub") {
                                holder.populateJenisCalon("Calon Gubernur", "Calon Wakil Gubernur")
                            } else {
                                holder.populateJenisCalon("Calon WaliKota", "Calon Wakil WaliKota")
                            }
                            holder.populateNomor(dataPaslon.nomor)

                            holder.itemView.setOnClickListener {
                                alertDialog(
                                    "Apakah anda yakin memilih pasangan calon ini?",
                                    "Konfirmasi Pilihan",
                                    model
                                )
                            }
                        }
                    }
            }
        }
        rv_content.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_content.adapter = adapter

    }

    private fun alertDialog(text: String, title: String, model: DataPemilihan) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setMessage(text)
        dialog.setTitle(title)
        dialog.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                val dbs =
                    FirebaseFirestore.getInstance().collection("users").document(dataPemilih!!.nik)
                dbs.update("status_" + status, true)
                val dbuser = FirebaseFirestore.getInstance().collection("pemilihan")
                dbuser.whereEqualTo("nama_wilayah", model.nama_wilayah)
                    .whereEqualTo("id_paslon", model.id_paslon)
                    .get()
                    .addOnSuccessListener {
                        for (document in it) {
                            val docgg = dbuser.document(document.id)
                            docgg.update("jlh_suara", FieldValue.increment(1))
                            Toast.makeText(this@Pemilihan,"Pemilihan Berhasil",Toast.LENGTH_SHORT).show()
                            val intent2 = Intent(this@Pemilihan, JenisPemilihan::class.java)
                            intent2.putExtra("dataExtra",dataPemilih)
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent2)
                            this@Pemilihan.finish()
                        }
                    }
            }
        })
        dialog.setNegativeButton("Tidak", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }

        })
        val alert = dialog.create()
        alert.show()
    }

    override fun onResume() {
        super.onResume()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            val v = itemView
        }

        fun populateNomor(nomor: Int) {
            val textView: TextView = itemView.findViewById(R.id.tv_noPaslon)
            textView.text = nomor.toString()
        }

        fun populateNama(nama1: String, nama2: String) {
            val tv1: TextView = itemView.findViewById(R.id.tv_nama1)
            tv1.text = nama1
            val tv2: TextView = itemView.findViewById(R.id.tv_nama2)
            tv2.text = nama2
        }

        fun populateFoto(foto: String, ctx: Context) {
            val ivFoto: ImageView = itemView.findViewById(R.id.iv_paslon)
            Glide.with(ctx).load(foto).into(ivFoto)
        }

        fun populateJenisCalon(calon1: String, calon2: String) {
            val tv1: TextView = itemView.findViewById(R.id.tv_calon1)
            tv1.text = calon1
            val tv2: TextView = itemView.findViewById(R.id.tv_calon2)
            tv2.text = calon2
        }
    }
}

