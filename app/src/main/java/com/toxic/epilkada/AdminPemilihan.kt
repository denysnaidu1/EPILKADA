package com.toxic.epilkada

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_admin_paslon.*
import kotlinx.android.synthetic.main.activity_admin_pemilihan.*

class AdminPemilihan : AppCompatActivity() {

    var firebaseRecyclerAdapter: FirestoreRecyclerAdapter<DataPemilihan,AdminPemilihan.ItemViewHolder>?=null
    var query: Query?=null
    var options: FirestoreRecyclerOptions<DataPemilihan>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pemilihan)

        searchPemilihan.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search(p0!!)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                search(p0!!)
                return false
            }

        })
        query = FirebaseFirestore.getInstance().collection("pemilihan").orderBy("id_paslon")
        options = FirestoreRecyclerOptions.Builder<DataPemilihan>()
            .setQuery(query!!, DataPemilihan::class.java).build()
        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataPemilihan, AdminPemilihan.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminPemilihan.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_admin_pemilihan, parent, false)
                return AdminPemilihan.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminPemilihan.ItemViewHolder,
                position: Int,
                model: DataPemilihan
            ) {
                holder.populateData(model.id_paslon,model.nama_wilayah,model.jlh_suara)
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilihan)
                    dialog.setMessage("Edit atau hapus data "+model.id_paslon+" dan "+model.nama_wilayah)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //camera!!.stopPreview()
                            val intentEdit= Intent(this@AdminPemilihan,EditPemilihan::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.id_pemilihan)
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilihan)
                            dialog1.setMessage("Yakin ingin menghapus data pemilihan ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseFirestore.getInstance().collection("pemilihan").document(model.id_pemilihan)
                                        .delete()
                                    Toast.makeText(this@AdminPemilihan,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
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

        rv_containerPemilihan.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rv_containerPemilihan.adapter=firebaseRecyclerAdapter

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

    private fun search(searchText: String) {
        query = FirebaseFirestore.getInstance().collection("pemilihan").orderBy("nama_wilayah").startAt(searchText.toLowerCase()).endAt(searchText.toLowerCase()+"\uf8ff")
        options = FirestoreRecyclerOptions.Builder<DataPemilihan>()
            .setQuery(query!!, DataPemilihan::class.java).build()
        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataPemilihan, AdminPemilihan.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminPemilihan.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_admin_pemilihan, parent, false)
                return AdminPemilihan.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminPemilihan.ItemViewHolder,
                position: Int,
                model: DataPemilihan
            ) {
                holder.populateData(model.id_paslon,model.nama_wilayah,model.jlh_suara)
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilihan)
                    dialog.setMessage("Edit atau hapus data "+model.id_paslon+" dan "+model.nama_wilayah)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //camera!!.stopPreview()
                            val intentEdit= Intent(this@AdminPemilihan,EditPemilihan::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.id_pemilihan)
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilihan)
                            dialog1.setMessage("Yakin ingin menghapus data pemilihan ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseFirestore.getInstance().collection("pemilihan").document(model.id_pemilihan)
                                        .delete()
                                    Toast.makeText(this@AdminPemilihan,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
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
        rv_containerPemilihan.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter!!.startListening()
    }

    fun addWilayah(view: View) {
        val intentBaru=Intent(this,AddPemilihan::class.java)
        startActivity(intentBaru)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun populateData(id: String, wilayah: String,suara: Int) {
            val tv1: TextView = itemView.findViewById(R.id.tv_idPaslon)
            tv1.text = id
            val tv2: TextView = itemView.findViewById(R.id.tvWilayah)
            tv2.text = wilayah
            val tv3: TextView = itemView.findViewById(R.id.tvSuara)
            tv3.text = suara.toString()
        }
    }

}