package com.toxic.epilkada

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class HasilPemilihan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemilihan)
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()

        val fragmentDetail=DetailPemilihan()
        fragmentTransaction.add(R.id.frameContainer,fragmentDetail,fragmentDetail.javaClass.simpleName)
        fragmentTransaction.commit()
    }

}