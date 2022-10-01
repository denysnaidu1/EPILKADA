package com.toxic.epilkada

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_admin_pemilihan.*
import kotlinx.android.synthetic.main.activity_admin_wilayah.*

class AdminWilayah : AppCompatActivity() {
    var firebaseRecyclerAdapter: FirestoreRecyclerAdapter<DataWilayah, AdminWilayah.ItemViewHolder>?=null
    var query: Query?=null
    var options: FirestoreRecyclerOptions<DataWilayah>?=null
    var statusPemilihan=false
    var idProv:ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_wilayah)

        searchWilayah.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search(p0!!,statusPemilihan)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                search(p0!!,statusPemilihan)
                return false
            }

        })

        query = FirebaseFirestore.getInstance().collection("wilayah").orderBy("id_wilayah")
        options = FirestoreRecyclerOptions.Builder<DataWilayah>()
            .setQuery(query!!, DataWilayah::class.java).build()
        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataWilayah, AdminWilayah.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminWilayah.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_wilayah, parent, false)
                return AdminWilayah.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminWilayah.ItemViewHolder,
                position: Int,
                model: DataWilayah
            ) {
                idProv.add(model.id_wilayah)
                holder.populateData(model.id_wilayah,model.nama,model.jlh_penduduk)
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                    dialog.setMessage("Edit atau hapus data "+model.id_wilayah+"-"+model.nama)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //camera!!.stopPreview()
                            val intentEdit= Intent(this@AdminWilayah,EditWilayah::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.id_wilayah+","+"provinsi")
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                            dialog1.setMessage("Yakin ingin menghapus data ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseFirestore.getInstance().collection("wilayah").document(model.id_wilayah)
                                        .delete()
                                    Toast.makeText(this@AdminWilayah,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
                                }
                            })
                            val alert1 = dialog1.create()
                            alert1.show()
                        }

                    })
                    val alert = dialog.create()
                    alert.show()
                }
            }
        }

        rv_containerWilayah.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rv_containerWilayah.adapter=firebaseRecyclerAdapter
    }

    override fun onStart() {
        super.onStart()
        firebaseRecyclerAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if(firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter!!.stopListening()
        }
    }

    private fun search(searchText: String,status:Boolean) {
        if(!status){
            query = FirebaseFirestore.getInstance().collection("wilayah").orderBy("nama").startAt(searchText).endAt(searchText+"\uf8ff")
        }else{
            query = FirebaseFirestore.getInstance().collection("wilayah").document("12").collection("kabupaten_kota").orderBy("nama").startAt(searchText).endAt(searchText+"\uf8ff")

        }
        Log.d("Teks Search",searchText)
//        query = FirebaseFirestore.getInstance().collection("wilayah").orderBy("nama").startAt(searchText).endAt(searchText+"\uf8ff")
        options = FirestoreRecyclerOptions.Builder<DataWilayah>()
            .setQuery(query!!, DataWilayah::class.java).build()
        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataWilayah, AdminWilayah.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminWilayah.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_wilayah, parent, false)
                return AdminWilayah.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminWilayah.ItemViewHolder,
                position: Int,
                model: DataWilayah
            ) {
                Log.d("weww",model.id_wilayah+"--"+model.nama+"--"+model.jlh_penduduk)
                holder.populateData(model.id_wilayah,model.nama,model.jlh_penduduk)
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                    dialog.setMessage("Edit atau hapus data "+model.id_wilayah+"-"+model.nama)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            if(status==false){
                                val intentEdit= Intent(this@AdminWilayah,EditWilayah::class.java)
                                intentEdit.putExtra("EXTRA_ID",model.id_wilayah+","+"provinsi")
                                startActivity(intentEdit)
                            }else {
                                val intentEdit = Intent(this@AdminWilayah, EditWilayah::class.java)
                                intentEdit.putExtra("EXTRA_ID", model.id_wilayah+","+"kabkot")
                                startActivity(intentEdit)
                            }
                        }
                    })
                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                            dialog1.setMessage("Yakin ingin menghapus data pemilihan ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    if(status==false){
                                        FirebaseFirestore.getInstance().collection("wilayah").document(model.id_wilayah)
                                            .delete()
                                        Toast.makeText(this@AdminWilayah,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
                                    }else {
                                        FirebaseFirestore.getInstance().collection("wilayah").document("12").collection("kabupaten_kota").document(model.id_wilayah)
                                            .delete()
                                        Toast.makeText(this@AdminWilayah,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                            val alert1 = dialog1.create()
                            alert1.show()
                        }

                    })
                    val alert = dialog.create()
                    alert.show()
                }
            }
        }
        rv_containerWilayah.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter!!.startListening()
    }

    fun addWilayah(view: View) {
        val intentTambah=Intent(this,AddWilayah::class.java)
        startActivity(intentTambah)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun populateData(id: String, wilayah: String,suara: Int) {
            val tv1: TextView = itemView.findViewById(R.id.tv_idWilayah)
            tv1.text = id
            val tv2: TextView = itemView.findViewById(R.id.tvNamaWilayah)
            tv2.text = wilayah
            val tv3: TextView = itemView.findViewById(R.id.tvPenduduk)
            tv3.text = suara.toString()
        }
    }

    fun filterProv(view: View) {
        statusPemilihan=false
        btnProvinsi.background=resources.getDrawable(R.drawable.btn_shape)
        btnKabKot.background=resources.getDrawable(R.drawable.btn_shape_inactive)
        btnProvinsi.textSize=16f
        btnKabKot.textSize=10f
        query = FirebaseFirestore.getInstance().collection("wilayah").orderBy("nama")
        options = FirestoreRecyclerOptions.Builder<DataWilayah>()
            .setQuery(query!!, DataWilayah::class.java).build()
        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataWilayah, AdminWilayah.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminWilayah.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_wilayah, parent, false)
                return AdminWilayah.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminWilayah.ItemViewHolder,
                position: Int,
                model: DataWilayah
            ) {
                holder.populateData(model.id_wilayah,model.nama,model.jlh_penduduk)
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                    dialog.setMessage("Edit atau hapus data "+model.id_wilayah+"-"+model.nama)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //camera!!.stopPreview()
                            val intentEdit= Intent(this@AdminWilayah,EditWilayah::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.id_wilayah+","+"provinsi")
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                            dialog1.setMessage("Yakin ingin menghapus data pemilihan ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseFirestore.getInstance().collection("wilayah").document(model.id_wilayah)
                                        .delete()
                                    Toast.makeText(this@AdminWilayah,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
                                }
                            })
                            val alert1 = dialog1.create()
                            alert1.show()
                        }

                    })
                    val alert = dialog.create()
                    alert.show()
                }
            }
        }
        rv_containerWilayah.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter!!.startListening()
    }
    fun filterKabKot(view: View) {
        statusPemilihan=true
        btnKabKot.background=resources.getDrawable(R.drawable.btn_shape)
        btnProvinsi.background=resources.getDrawable(R.drawable.btn_shape_inactive)
        btnProvinsi.textSize=10f
        btnKabKot.textSize=16f
        for (id in idProv){
            query = FirebaseFirestore.getInstance().collection("wilayah").document(id).collection("kabupaten_kota").orderBy("nama")
            options = FirestoreRecyclerOptions.Builder<DataWilayah>()
                .setQuery(query!!, DataWilayah::class.java).build()
            firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataWilayah, AdminWilayah.ItemViewHolder>(options!!){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminWilayah.ItemViewHolder {
                    val view =
                        LayoutInflater.from(parent.context).inflate(R.layout.item_wilayah, parent, false)
                    return AdminWilayah.ItemViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: AdminWilayah.ItemViewHolder,
                    position: Int,
                    model: DataWilayah
                ) {
                    holder.populateData(model.id_wilayah,model.nama,model.jlh_penduduk)
                    holder.itemView.setOnClickListener {
                        val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                        dialog.setMessage("Edit atau hapus data "+model.id_wilayah+"-"+model.nama)
                        dialog.setTitle("Pilih Aksi")
                        dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {

                            }

                        })
                        dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                //camera!!.stopPreview()
                                val intentEdit= Intent(this@AdminWilayah,EditWilayah::class.java)
                                intentEdit.putExtra("EXTRA_ID",model.id_wilayah+","+"kabkot")
                                startActivity(intentEdit)
                            }
                        })
                        dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminWilayah)
                                dialog1.setMessage("Yakin ingin menghapus data pemilihan ini?")
                                dialog1.setTitle("Hapus Data")
                                dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        Log.d("hapus data",model.id_wilayah.substring(2))
                                        FirebaseFirestore.getInstance().collection("wilayah").document(id).collection("kabupaten_kota").document(model.id_wilayah.substring(2))
                                            .delete()
                                        Toast.makeText(this@AdminWilayah,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
                                    }
                                })
                                val alert1 = dialog1.create()
                                alert1.show()
                            }

                        })
                        val alert = dialog.create()
                        alert.show()
                    }
                }
            }
            rv_containerWilayah.adapter=firebaseRecyclerAdapter
            firebaseRecyclerAdapter!!.startListening()
        }
    }

}