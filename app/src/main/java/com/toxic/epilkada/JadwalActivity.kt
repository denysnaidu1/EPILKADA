package com.toxic.epilkada

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.toxic.epilkada.com.toxic.epilkada.DataJadwal
import kotlinx.android.synthetic.main.activity_jadwal.*
import java.text.SimpleDateFormat

class JadwalActivity : AppCompatActivity() {

    var firebaseRecyclerAdapter: FirestoreRecyclerAdapter<DataJadwal, JadwalActivity.ItemViewHolder>? =
        null
    var query: Query? = null
    var options: FirestoreRecyclerOptions<DataJadwal>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal)

        //Membuat query untuk menampilkan data jadwal
        query = FirebaseFirestore.getInstance().collection("jadwal").orderBy("id_jadwal")
        options = FirestoreRecyclerOptions.Builder<DataJadwal>()
            .setQuery(query!!, DataJadwal::class.java).build()

        //Menampilkan data jadwal dalam recycler view
        firebaseRecyclerAdapter =
            object :
                FirestoreRecyclerAdapter<DataJadwal, JadwalActivity.ItemViewHolder>(options!!) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): JadwalActivity.ItemViewHolder {
                    val view =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.list_jadwal, parent, false)
                    return JadwalActivity.ItemViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: JadwalActivity.ItemViewHolder,
                    position: Int,
                    model: DataJadwal
                ) {
                    Log.d("DataWilayah", model.id_wilayah)
                    if (model.id_wilayah.length == 2) {
                        FirebaseFirestore.getInstance().collection("wilayah")
                            .document(model.id_wilayah)
                            .get().addOnSuccessListener {
                                holder.populateData(it["nama"].toString(), model.tgl_pelaksanaan!!)
                            }
                    } else if (model.id_wilayah.length == 4) {
                        var idProv = model.id_wilayah.substring(0, 2)
                        Log.d("Slices Id", idProv + " ---" + model.id_wilayah)
                        FirebaseFirestore.getInstance().collection("wilayah").document(idProv)
                            .collection("kabupaten_kota")
                            .whereEqualTo("id_wilayah", model.id_wilayah).get()
                            .addOnSuccessListener { items ->
                                for (item in items) {
                                    Log.d("Itemss", item["nama"].toString())
                                    holder.populateData(
                                        item["nama"].toString(),
                                        model.tgl_pelaksanaan!!
                                    )
                                }
                            }
                    }
                }
            }

        rv_Jadwal.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_Jadwal.adapter = firebaseRecyclerAdapter
    }

    override fun onStart() {
        super.onStart()
        firebaseRecyclerAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter!!.stopListening()
        }
    }

    //ViewHolder untuk menampung dan memasukkan data model kedalam objek view
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun populateData(namaWilayah: String, tanggal: Timestamp) {
            val tv1: TextView = itemView.findViewById(R.id.tvWilayahJadwal)
            tv1.text = namaWilayah
            val timeFormat = SimpleDateFormat("hh:mm:ss").format(tanggal.toDate())
            val tv2: TextView = itemView.findViewById(R.id.tvWaktu)
            tv2.text = timeFormat.toString()
            val dataFormat = SimpleDateFormat("dd MMMM yyyy").format(tanggal.toDate())
            val tv4: TextView = itemView.findViewById(R.id.tv_Tanggal)
            tv4.text = dataFormat
        }
    }
}