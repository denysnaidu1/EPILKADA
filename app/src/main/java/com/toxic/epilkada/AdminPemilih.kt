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
import com.toxic.epilkada.com.toxic.epilkada.DataPemilihUser
import kotlinx.android.synthetic.main.activity_admin_pemilih.*

class AdminPemilih : AppCompatActivity() {

    var firebaseRecyclerAdapter: FirestoreRecyclerAdapter<DataPemilihUser, AdminPemilih.ItemViewHolder>?=null
    var query: Query?=null
    var options: FirestoreRecyclerOptions<DataPemilihUser>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pemilih)

        //Listener untuk mencari data pemilih berdasarkan NIK
        searchPemilih.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search(p0!!)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                search(p0!!)
                return false
            }

        })

        query = FirebaseFirestore.getInstance().collection("users").orderBy("nik")
        options = FirestoreRecyclerOptions.Builder<DataPemilihUser>()
            .setQuery(query!!, DataPemilihUser::class.java).build()

        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataPemilihUser, AdminPemilih.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminPemilih.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_pemilih_user, parent, false)
                return AdminPemilih.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminPemilih.ItemViewHolder,
                position: Int,
                model: DataPemilihUser
            ) {
                //Fungsi untuk menyingkat nama pemilih jika lebih dari 2 kata
                val bagiNama=model.nama.split(' ')
                if(bagiNama.size>2){
                    var newName=""
                    for(a in 0 until bagiNama.size){
                        if(a==0){
                            newName+=bagiNama[a]
                        }
                        else if(a>1){
                            newName=newName+" "+bagiNama[a][0]+". "
                        }
                        else if(a<3){
                            newName=newName+" "+bagiNama[a]
                        }else{
                            break
                        }
                    }
                    holder.populateData((position+1).toString(),model.nik,newName)
                }else{
                    holder.populateData((position+1).toString(),model.nik,model.nama)
                }
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilih)
                    dialog.setMessage("Edit atau hapus data "+model.nik+"-"+model.nama)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val intentEdit= Intent(this@AdminPemilih,EditPemilih::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.nik)
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilih)
                            dialog1.setMessage("Yakin ingin menghapus data warga ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    var help=0
                                    for(a in 0 until model.foto.size){
                                        FirebaseStorage.getInstance().getReferenceFromUrl(model.foto[a]).delete()
                                        FirebaseFirestore.getInstance().collection("users").document(model.nik)
                                            .delete()
                                        if(help==model.foto.size-1){
                                            Toast.makeText(this@AdminPemilih,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
                                        }
                                        help++
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

        rv_containerPemilih.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rv_containerPemilih.adapter=firebaseRecyclerAdapter

    }

    private fun search(searchText: String) {
        query = FirebaseFirestore.getInstance().collection("users").orderBy("nik").startAt(searchText).endAt(searchText+"\uf8ff")
        options = FirestoreRecyclerOptions.Builder<DataPemilihUser>()
            .setQuery(query!!, DataPemilihUser::class.java).build()

        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataPemilihUser, AdminPemilih.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminPemilih.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_pemilih_user, parent, false)
                return AdminPemilih.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminPemilih.ItemViewHolder,
                position: Int,
                model: DataPemilihUser
            ) {
                //Fungsi untuk menyingkat nama pemilih jika lebih dari 2 kata
                val bagiNama=model.nama.split(' ')
                if(bagiNama.size>2){
                    var newName=""
                    for(a in 0 until bagiNama.size){
                        if(a==0){
                            newName+=bagiNama[a]
                        }
                        else if(a>1){
                            newName=newName+" "+bagiNama[a][0]+". "
                        }
                        else if(a<3){
                            newName=newName+" "+bagiNama[a]
                        }else{
                            break
                        }
                    }
                    holder.populateData((position+1).toString(),model.nik,newName)
                }else{
                    holder.populateData((position+1).toString(),model.nik,model.nama)
                }
                holder.itemView.setOnClickListener {
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilih)
                    dialog.setMessage("Edit atau hapus data "+model.nik+"-"+model.nama)
                    dialog.setTitle("Pilih Aksi")
                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }

                    })
                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //camera!!.stopPreview()
                            val intentEdit= Intent(this@AdminPemilih,EditPemilih::class.java)
                            intentEdit.putExtra("EXTRA_ID",model.nik)
                            startActivity(intentEdit)
                        }
                    })
                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminPemilih)
                            dialog1.setMessage("Yakin ingin menghapus data pemilih ini?")
                            dialog1.setTitle("Hapus Data")
                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    var help=0
                                    for(a in 0 until model.foto.size){
                                        FirebaseStorage.getInstance().getReferenceFromUrl(model.foto[a]).delete()
                                        FirebaseFirestore.getInstance().collection("users").document(model.nik)
                                            .delete()
                                        if(help==model.foto.size-1){
                                            Toast.makeText(this@AdminPemilih,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
                                        }
                                        help++
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
        rv_containerPemilih.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter!!.startListening()
    }

    fun addPemilih(view: View) {
        val intentAktivitas=Intent(this,AddPemilih::class.java)
        startActivity(intentAktivitas)
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

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun populateData(angka: String, nik: String,nama: String) {
            val tv1: TextView = itemView.findViewById(R.id.tvAngka)
            tv1.text = angka
            val tv2: TextView = itemView.findViewById(R.id.tvNamaPemilih)
            tv2.text = nama
            val tv4: TextView = itemView.findViewById(R.id.tvNIK)
            tv4.text = nik
        }
    }
}