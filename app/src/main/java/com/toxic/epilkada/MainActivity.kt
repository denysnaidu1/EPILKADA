package com.toxic.epilkada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var intentAksi:Intent?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun fabAction(view: View) {
        intentAksi=Intent(this, Identifikasi::class.java)
        intentAksi!!.putExtra("EXTRA_STATUS","user")
        startActivity(intentAksi)
    }
    fun iconAdminAction(view: View) {
        intentAksi=Intent(this, LoginAdmin::class.java)
        intentAksi!!.putExtra("EXTRA_STATUS","admin")
        startActivity(intentAksi)
    }
    fun lihatHasilPemilihan(view: View) {
        intentAksi=Intent(this, HasilPemilihan::class.java)
        startActivity(intentAksi)
    }
    fun lihatJadwal(view: View) {
        intentAksi=Intent(this, JadwalActivity::class.java)
        startActivity(intentAksi)
    }
}