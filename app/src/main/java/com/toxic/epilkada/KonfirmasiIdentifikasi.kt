package com.toxic.epilkada

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment



class KonfirmasiIdentifikasi : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_konfirmasi_identifikasi, container, true)
        val btnYes = view.findViewById<Button>(R.id.btn_ya)
        val btnNo = view.findViewById<Button>(R.id.btn_no)
        val data = this.arguments?.get("data_Extra") as pemilih
        val tvData = view.findViewById<TextView>(R.id.tv_data)
        tvData.text =
            "Data:\n" + "Nama: " + data.nama + "\nAgama: " + data.agama + "\nJenis Kelamin: " + data.jk + "\nTempat Lahir: " + data.tpt_lahir
        btnYes.setOnClickListener {
            val intent = Intent(this.activity, IdentifikasiWajah::class.java)
            intent.putExtra("dataParcel",data)
            startActivity(intent)
            this.dismiss()
        }
        btnNo.setOnClickListener {
            this.dismiss()
        }
        return view
    }
}