package com.toxic.epilkada

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_admin_paslon.*
import kotlinx.android.synthetic.main.activity_identifikasi.*

class AdminPaslon : AppCompatActivity() {
    var firebaseRecyclerAdapter:FirestoreRecyclerAdapter<DataPaslon,ItemViewHolder>?=null
    var query:Query?=null
    var options:FirestoreRecyclerOptions<DataPaslon>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_paslon)

        searchPaslon.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search(p0!!)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                search(p0!!)
                return false
            }

        })
        query = FirebaseFirestore.getInstance().collection("paslon").orderBy("id_paslon")
        options = FirestoreRecyclerOptions.Builder<DataPaslon>()
            .setQuery(query!!, DataPaslon::class.java).build()


        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataPaslon,ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_paslon, parent, false)
                return AdminPaslon.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ItemViewHolder,
                position: Int,
                model: DataPaslon
            ) {
                holder.populateData(model.id_paslon,model.nama1,model.nama2,model.nomor)
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminPaslon)
                    dialog.setMessage("Edit atau hapus data "+model.nama1+" dan "+model.nama2)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object :DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //camera!!.stopPreview()
                            val intentEdit=Intent(this@AdminPaslon,EditPaslon::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.id_paslon)
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object:DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminPaslon)
                            dialog1.setMessage("Yakin ingin menghapus data paslon ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseStorage.getInstance().getReferenceFromUrl(model.foto).delete()
                                    FirebaseFirestore.getInstance().collection("paslon").document(model.id_paslon)
                                        .delete()
                                    Toast.makeText(this@AdminPaslon,"Berhasil hapus data",Toast.LENGTH_SHORT).show()
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

        rv_containerPaslon.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_containerPaslon.adapter=firebaseRecyclerAdapter
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

    private fun search(searchText:String){
        query = FirebaseFirestore.getInstance().collection("paslon").orderBy("id_paslon").startAt(searchText).endAt(searchText+"\uf8ff")
        Log.d("search",searchText)
        options = FirestoreRecyclerOptions.Builder<DataPaslon>()
            .setQuery(query!!, DataPaslon::class.java).build()
        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataPaslon,ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_paslon, parent, false)
                return AdminPaslon.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ItemViewHolder,
                position: Int,
                model: DataPaslon
            ) {
                holder.populateData(model.id_paslon,model.nama1,model.nama2,model.nomor)
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminPaslon)
                    dialog.setMessage("Edit atau hapus data "+model.nama1+" dan "+model.nama2)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object :DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //camera!!.stopPreview()
                           val intentEdit=Intent(this@AdminPaslon,EditPaslon::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.id_paslon)
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object:DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminPaslon)
                            dialog1.setMessage("Yakin ingin menghapus data paslon ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseStorage.getInstance().getReferenceFromUrl(model.foto).delete()
                                    FirebaseFirestore.getInstance().collection("paslon").document(model.id_paslon)
                                        .delete()
                                    Toast.makeText(this@AdminPaslon,"Berhasil hapus data",Toast.LENGTH_SHORT).show()
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
        rv_containerPaslon.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter!!.startListening()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun populateData(id: String, nama1: String,nama2: String,nomor:Int) {
            val tv1: TextView = itemView.findViewById(R.id.tv_id)
            tv1.text = id
            val tv2: TextView = itemView.findViewById(R.id.tvNama1)
            tv2.text = nama1
            val tv3: TextView = itemView.findViewById(R.id.tvNama2)
            tv3.text = nama2
            val tv4: TextView = itemView.findViewById(R.id.tvNomor)
            tv4.text = nomor.toString()
        }
    }

    fun addPaslon(view: View) {
        var intentAktivitas= Intent(this,AddPaslon::class.java)
        startActivity(intentAktivitas)
    }
}