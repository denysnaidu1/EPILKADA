package com.toxic.epilkada

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class FragmentHasil : Fragment() {
    var totalSuaraMasuk = 0
    var query: Query? = null
    var activity1: HasilPemilihan? = null
    private var adapter: FirestoreRecyclerAdapter<DataPemilihan, HasilViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hasil, container, false)
        val dataBundle = arguments!!["DATABUNDLE"] as ArrayList<String>
        val dialog = ProgressDialog(this.context)
        dialog.setMessage("Mempersiapkan Data...\nProses membutuhkan jaringan internet")
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog.show()
        var jumlah_Penduduk = 0
        val tv_suaraMasuk = view.findViewById<TextView>(R.id.tv_masukSuara)
        val tv_sisaSuara = view.findViewById<TextView>(R.id.tv_sisaSuara)
        val rv_hasil = view.findViewById<RecyclerView>(R.id.rv_hasil)
        val tvPerolehan: TextView = view.findViewById(R.id.tvPerolehan)
        var status = ""
        if (dataBundle.size < 3) {
            query = FirebaseFirestore.getInstance().collection("pemilihan")
                .whereEqualTo("nama_wilayah", dataBundle[1].toLowerCase())
                .orderBy("id_paslon", Query.Direction.ASCENDING)
            tvPerolehan.text = "PILKADA " + dataBundle[1]
            status = "provinsi"
            FirebaseFirestore.getInstance().collection("pemilihan")
                .whereEqualTo("nama_wilayah", dataBundle[1].toLowerCase())
                .get().addOnSuccessListener {
                    if (it.isEmpty) {
                        Log.d("GG", "asss")
                        dialog.dismiss()
                        showAlert()
                    }
                }
        } else {
            query = FirebaseFirestore.getInstance().collection("pemilihan")
                .whereEqualTo("nama_wilayah", dataBundle[2].toLowerCase())
                .orderBy("id_paslon", Query.Direction.ASCENDING)
            tvPerolehan.text = "PILKADA " + dataBundle[2]
            status = "kabkot"
            FirebaseFirestore.getInstance().collection("pemilihan")
                .whereEqualTo("nama_wilayah", dataBundle[2].toLowerCase())
                .get().addOnSuccessListener {
                    if (it.isEmpty) {
                        dialog.dismiss()
                        showAlert()
                    }
                }
        }
        var help = 0
        var ctr = 0
        var dataSuara: ArrayList<Int> = ArrayList()
        var data: Array<Int> = arrayOf(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
        )
        var high = 0
        var options: FirestoreRecyclerOptions<DataPemilihan>? = null
        options = FirestoreRecyclerOptions.Builder<DataPemilihan>()
            .setQuery(query!!, DataPemilihan::class.java).build()
        adapter = object : FirestoreRecyclerAdapter<DataPemilihan, HasilViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HasilViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_hasil, parent, false)
                Log.d("Duluan", "ini duluan")
                return HasilViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: HasilViewHolder,
                position: Int,
                model: DataPemilihan
            ) {
                val docRef =
                    FirebaseFirestore.getInstance().collection("paslon").document(model.id_paslon)
                docRef.get().addOnSuccessListener {
                    if (it.exists()) {
                        Log.d("ini dipanggil", "asda")
                        val wilayahRef = FirebaseFirestore.getInstance().collection("wilayah")
                            .document(dataBundle[0])
                        if (status == "provinsi") {
                            wilayahRef.get().addOnSuccessListener { wilayahSnapshot ->
                                FirebaseFirestore.getInstance().collection("pemilihan")
                                    .whereEqualTo("nama_wilayah", dataBundle[1].toLowerCase())
                                    .get().addOnSuccessListener { querySnapShot ->
                                        high = querySnapShot.size()
                                        jumlah_Penduduk =
                                            Integer.parseInt(wilayahSnapshot["jlh_penduduk"].toString())
                                        val dataPaslon: Paslon = Paslon(
                                            it["nama1"].toString(),
                                            it["nama2"].toString(),
                                            Integer.parseInt(it["nomor"].toString()),
                                            it["nik1"].toString(),
                                            it["nik2"].toString(),
                                            it["partai"].toString(),
                                            it["foto"].toString()
                                        )
                                        dataSuara.add(model.jlh_suara)
                                        Log.d("Sukses", " Nilai help " + help.toString())
                                        Log.d("Sukses", "nilai posisi " + position.toString())
                                        holder.populateFoto(
                                            dataPaslon.foto,
                                            this@FragmentHasil.context!!
                                        )
                                        holder.populateNomor(dataPaslon.nomor)
                                        holder.populateSuara(model.jlh_suara, jumlah_Penduduk)
                                        Log.d("Sukses", "loading selesai")
                                        data[position] = model.jlh_suara
                                        ctr++
                                        if (ctr == help) {
                                            for (j in 0 until high) {
                                                totalSuaraMasuk += data[j]
                                                Log.d("Angka", data[j].toString())
                                            }
                                            val suaraMasuk: Double =
                                                (totalSuaraMasuk / jumlah_Penduduk.toDouble()) * 100
                                            tv_suaraMasuk.text = "Suara Masuk: " + String.format(
                                                "%.2f",
                                                suaraMasuk
                                            ) + "%"
                                            tv_sisaSuara.text = "Sisa Suara: " + String.format(
                                                "%.2f",
                                                (100.0 - suaraMasuk)
                                            ) + "%"
                                            dialog.dismiss()
                                            ctr = 0
                                            help = 0
                                            totalSuaraMasuk = 0
                                        }
                                    }
                            }
                        } else if (status == "kabkot") {
                            wilayahRef.collection("kabupaten_kota")
                                .whereEqualTo("nama", dataBundle[2]).get()
                                .addOnSuccessListener { wilayahSnapshot ->
                                    FirebaseFirestore.getInstance().collection("pemilihan")
                                        .whereEqualTo("nama_wilayah", dataBundle[2].toLowerCase())
                                        .get().addOnSuccessListener { querySnapShot ->
                                            high = querySnapShot.size()
                                            jumlah_Penduduk =
                                                Integer.parseInt(wilayahSnapshot.documents[0]["jlh_penduduk"].toString())
                                            val dataPaslon: Paslon = Paslon(
                                                it["nama1"].toString(),
                                                it["nama2"].toString(),
                                                Integer.parseInt(it["nomor"].toString()),
                                                it["nik1"].toString(),
                                                it["nik2"].toString(),
                                                it["partai"].toString(),
                                                it["foto"].toString()
                                            )
                                            Log.d("Nomor", it["nomor"].toString())
                                            dataSuara.add(model.jlh_suara)
                                            Log.d("Sukses", " Nilai help " + help.toString())
                                            Log.d("Sukses", "nilai posisi " + position.toString())
                                            holder.populateFoto(
                                                dataPaslon.foto,
                                                this@FragmentHasil.context!!
                                            )
                                            holder.populateNomor(dataPaslon.nomor)
                                            holder.populateSuara(model.jlh_suara, jumlah_Penduduk)
                                            Log.d("Sukses", "loading selesai")
                                            data[position] = model.jlh_suara
                                            ctr++
                                            if (ctr == help) {
                                                for (j in 0 until high) {
                                                    totalSuaraMasuk += data[j]
                                                    Log.d("Angka", data[j].toString())
                                                }
                                                val suaraMasuk: Double =
                                                    (totalSuaraMasuk / jumlah_Penduduk.toDouble()) * 100
                                                tv_suaraMasuk.text = "Suara Masuk: " + String.format(
                                                    "%.2f",
                                                    suaraMasuk
                                                ) + "%"
                                                tv_sisaSuara.text = "Sisa Suara: " + String.format(
                                                    "%.2f",
                                                    (100.0 - suaraMasuk)
                                                ) + "%"
                                                dialog.dismiss()
                                                ctr = 0
                                                help = 0
                                                totalSuaraMasuk = 0
                                            }
                                        }
                                }
                        }
                    }
                }
                help++
            }
        }
        rv_hasil.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rv_hasil.adapter = adapter
        return view
    }

    private fun showAlert() {
        val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@FragmentHasil.context)
        dialog1.setMessage("Pemilihan pada daerah ini belum dimulai atau belum ada.")
        dialog1.setTitle("Data Tidak Ada")
        dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                activity!!.onBackPressed()
            }
        })
        val alert1 = dialog1.create()
        alert1.show()
    }


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        activity1 = activity as HasilPemilihan
        activity1!!.supportActionBar!!.hide()
    }

    class HasilViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun populateNomor(nomor: Int) {
            val tv_nomor = itemView.findViewById<TextView>(R.id.tv_nomor)
            tv_nomor.text = "Paslon - " + nomor.toString()
        }

        fun populateSuara(jumlah: Int, penduduk: Int) {
            val tv_suara = itemView.findViewById<TextView>(R.id.tv_angka)
            var tempSuara: Double = (jumlah / penduduk.toDouble()) * 100
            tv_suara.text = String.format("%.2f", tempSuara) + "%"
        }

        fun populateFoto(uri: String, ctx: Context) {
            val iv_foto = itemView.findViewById<ImageView>(R.id.iv_foto)
            Glide.with(ctx).load(uri).into(iv_foto)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter!!.startListening()
    }

    override fun onDetach() {
        super.onDetach()
        activity1!!.supportActionBar!!.show()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }
}