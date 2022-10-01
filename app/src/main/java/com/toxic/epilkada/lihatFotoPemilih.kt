package com.toxic.epilkada

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toxic.epilkada.com.toxic.epilkada.fotoPemilih
import kotlinx.android.synthetic.main.list_item_foto.view.*
import org.jetbrains.anko.find
import org.w3c.dom.Text


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class lihatFotoPemilih : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_lihat_foto_pemilih, container, true)
        //val gridLayout=view.findViewById<GridView>(R.id.gridContent)
        val dataFoto=this.arguments!!.getParcelable<fotoPemilih>("BUNDLE_FOTO")
        Log.d("Fragment",dataFoto!!.listFoto.size.toString())
        val rv=view.findViewById<RecyclerView>(R.id.rvContent)
        val layoutManager=GridLayoutManager(this.context,4)
        val adapter=RecycleViewAdapter(dataFoto,this.context!!)
        var btnConfirm: Button =view.findViewById(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            this.dismiss()
        }
        rv.layoutManager=layoutManager
        rv.adapter=adapter
        rv.setHasFixedSize(true)
        //val fotoAdapter=FotoAdapter(this.context!!,dataFoto!!)
        //gridLayout.adapter=fotoAdapter
        return view
    }

    class RecycleViewAdapter(var arr: fotoPemilih,var ctx:Context) :
        RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder>() {
        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val ivImage=itemView.findViewById<ImageView>(R.id.imageItem)
            val tvItem:TextView= itemView.findViewById(R.id.tv_itemText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=LayoutInflater.from(parent.context).inflate(R.layout.list_item_foto,parent,false)
            val myViewHolder:MyViewHolder= MyViewHolder(view)
            return myViewHolder
        }

        override fun getItemCount(): Int {
            return arr.listFoto.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.ivImage.setImageBitmap(arr.listFoto[position])
            holder.tvItem.text="Gambar "+position.toString()
            holder.ivImage.setOnClickListener {
                Log.d("Testing", position.toString())

                val dialog: AlertDialog.Builder = AlertDialog.Builder(ctx)
                dialog.setMessage("Yakin ingin menghapus foto?")
                dialog.setTitle("Hapus Foto")
                dialog.setPositiveButton(
                    "Ya",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            arr.listFoto.removeAt(position)
                            arr.index.add(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position,arr.listFoto.size)
                        }
                    })
                dialog.setNegativeButton("Tidak",
                object :DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }
                })
                val alert = dialog.create()
                alert.show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment lihatFotoPemilih.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            lihatFotoPemilih().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}