package com.toxic.epilkada

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.toxic.epilkada.com.toxic.epilkada.DataJadwal
import com.toxic.epilkada.com.toxic.epilkada.DataPemilihUser
import kotlinx.android.synthetic.main.activity_add_pemilih.*
import kotlinx.android.synthetic.main.activity_admin_jadwal.*
import kotlinx.android.synthetic.main.activity_admin_pemilih.*
import java.text.SimpleDateFormat
import java.util.*

class AdminJadwal : AppCompatActivity() {

    var firebaseRecyclerAdapter: FirestoreRecyclerAdapter<DataJadwal ,AdminJadwal.ItemViewHolder>?=null
    var query: Query?=null
    var options: FirestoreRecyclerOptions<DataJadwal>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_jadwal)
        searchJadwal.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search(p0!!)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                search(p0!!)
                return false
            }

        })
        query = FirebaseFirestore.getInstance().collection("jadwal").orderBy("id_jadwal")
        options = FirestoreRecyclerOptions.Builder<DataJadwal>()
            .setQuery(query!!, DataJadwal::class.java).build()

        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataJadwal, AdminJadwal.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminJadwal.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
                return AdminJadwal.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminJadwal.ItemViewHolder,
                position: Int,
                model: DataJadwal
            ) {
                val newIdProv=model.id_wilayah.substring(0,2)
                if(model.id_wilayah.length==4){
                    FirebaseFirestore.getInstance().collection("wilayah").document(newIdProv).collection("kabupaten_kota").whereEqualTo("id_wilayah",model.id_wilayah)
                        .get().addOnSuccessListener {
                            it.forEach { item->
                                holder.populateData(model.id_jadwal,item["nama"].toString(),model.tgl_pelaksanaan!!)
                                val nama=item["nama"].toString()
                                holder.itemView.setOnClickListener {
                                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                    dialog.setMessage("Edit atau hapus data "+model.id_jadwal+"-"+nama)
                                    dialog.setTitle("Pilih Aksi")
                                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                                        override fun onClick(p0: DialogInterface?, p1: Int) {

                                        }

                                    })
                                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            val intentEdit= Intent(this@AdminJadwal,EditJadwal::class.java)
                                            intentEdit.putExtra("EXTRA_ID_KABKOT",model.id_jadwal+","+nama+","+model.id_wilayah)
                                            startActivity(intentEdit)
                                        }
                                    })
                                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                            dialog1.setMessage("Yakin ingin menghapus jadwal ini?")
                                            dialog1.setTitle("Hapus Jadwal")
                                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                                    FirebaseFirestore.getInstance().collection("jadwal").document(model.id_jadwal)
                                                        .delete()
                                                    Toast.makeText(this@AdminJadwal,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
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
                }else{
                    FirebaseFirestore.getInstance().collection("wilayah").document(newIdProv)
                        .get().addOnSuccessListener {
                            holder.populateData(model.id_jadwal,it["nama"].toString(),model.tgl_pelaksanaan!!)
                            val nama=it["nama"].toString()
                            holder.itemView.setOnClickListener {
                                val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                dialog.setMessage("Edit atau hapus data "+model.id_jadwal+"-"+nama)
                                dialog.setTitle("Pilih Aksi")
                                dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {

                                    }

                                })
                                dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        val intentEdit= Intent(this@AdminJadwal,EditJadwal::class.java)
                                        intentEdit.putExtra("EXTRA_ID",model.id_jadwal+","+nama+","+model.id_wilayah)
                                        startActivity(intentEdit)
                                    }
                                })
                                dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                        dialog1.setMessage("Yakin ingin menghapus jadwal ini?")
                                        dialog1.setTitle("Hapus Jadwal")
                                        dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                                FirebaseFirestore.getInstance().collection("jadwal").document(model.id_jadwal)
                                                    .delete()
                                                Toast.makeText(this@AdminJadwal,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
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

            }
        }

        rv_containerJadwal.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rv_containerJadwal.adapter=firebaseRecyclerAdapter
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
        query = FirebaseFirestore.getInstance().collection("jadwal").orderBy("id_jadwal").startAt(searchText).endAt(searchText+"\uf8ff")
        options = FirestoreRecyclerOptions.Builder<DataJadwal>()
            .setQuery(query!!, DataJadwal::class.java).build()
        firebaseRecyclerAdapter=object :FirestoreRecyclerAdapter<DataJadwal, AdminJadwal.ItemViewHolder>(options!!){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminJadwal.ItemViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
                return AdminJadwal.ItemViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdminJadwal.ItemViewHolder,
                position: Int,
                model: DataJadwal
            ) {
                val newIdProv=model.id_wilayah.substring(0,2)
                if(model.id_wilayah.length==4){
                    FirebaseFirestore.getInstance().collection("wilayah").document(newIdProv).collection("kabupaten_kota").whereEqualTo("id_wilayah",model.id_wilayah)
                        .get().addOnSuccessListener {
                            it.forEach { item->
                                holder.populateData(model.id_jadwal,item["nama"].toString(),model.tgl_pelaksanaan!!)
                                val nama=item["nama"].toString()
                                holder.itemView.setOnClickListener {
                                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                    dialog.setMessage("Edit atau hapus data "+model.id_jadwal+"-"+nama)
                                    dialog.setTitle("Pilih Aksi")
                                    dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                                        override fun onClick(p0: DialogInterface?, p1: Int) {

                                        }

                                    })
                                    dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            val intentEdit= Intent(this@AdminJadwal,EditJadwal::class.java)
                                            intentEdit.putExtra("EXTRA_ID_KABKOT",model.id_jadwal+","+nama+","+model.id_wilayah)
                                            startActivity(intentEdit)
                                        }
                                    })
                                    dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                            dialog1.setMessage("Yakin ingin menghapus jadwal ini?")
                                            dialog1.setTitle("Hapus Jadwal")
                                            dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                                    FirebaseFirestore.getInstance().collection("jadwal").document(model.id_jadwal)
                                                        .delete()
                                                    Toast.makeText(this@AdminJadwal,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
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
                }else{
                    FirebaseFirestore.getInstance().collection("wilayah").document(newIdProv)
                        .get().addOnSuccessListener {
                            holder.populateData(model.id_jadwal,it["nama"].toString(),model.tgl_pelaksanaan!!)
                            val nama=it["nama"].toString()
                            holder.itemView.setOnClickListener {
                                val dialog: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                dialog.setMessage("Edit atau hapus data "+model.id_jadwal+"-"+nama)
                                dialog.setTitle("Pilih Aksi")
                                dialog.setNeutralButton("Batal",object : DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {

                                    }

                                })
                                dialog.setPositiveButton("Edit", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        val intentEdit= Intent(this@AdminJadwal,EditJadwal::class.java)
                                        intentEdit.putExtra("EXTRA_ID",model.id_jadwal+","+nama+","+model.id_wilayah)
                                        startActivity(intentEdit)
                                    }
                                })
                                dialog.setNegativeButton("Hapus",object: DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        val dialog1: AlertDialog.Builder = AlertDialog.Builder(this@AdminJadwal)
                                        dialog1.setMessage("Yakin ingin menghapus jadwal ini?")
                                        dialog1.setTitle("Hapus Jadwal")
                                        dialog1.setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                                FirebaseFirestore.getInstance().collection("jadwal").document(model.id_jadwal)
                                                    .delete()
                                                Toast.makeText(this@AdminJadwal,"Berhasil hapus data", Toast.LENGTH_SHORT).show()
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
            }
        }
        rv_containerJadwal.adapter=firebaseRecyclerAdapter
        firebaseRecyclerAdapter!!.startListening()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun populateData(angka: String, namaWilayah: String,tanggal: Timestamp) {
            val tv1: TextView = itemView.findViewById(R.id.tv_idJadwal)
            tv1.text = angka
            val tv2: TextView = itemView.findViewById(R.id.tvNamaWilayahJadwal)
            tv2.text = namaWilayah
            val dataFormat = SimpleDateFormat("dd/MM/yyyy").format(tanggal.toDate())
            val tv4: TextView = itemView.findViewById(R.id.tvTanggal)
            tv4.text = dataFormat
        }
    }

    fun addJadwal(view: View) {
        val intentAktivitas=Intent(this,AddJadwal::class.java)
        startActivity(intentAktivitas)
    }
}