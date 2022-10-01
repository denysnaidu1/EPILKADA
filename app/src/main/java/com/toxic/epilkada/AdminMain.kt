package com.toxic.epilkada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AdminMain : AppCompatActivity() {
    var intent1: Intent?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)
    
    }

    fun goToPaslon(view: View) {
        intent1=Intent(this,AdminPaslon::class.java)
        startActivity(intent1)
    }
    fun goToPemilihan(view: View) {
        intent1=Intent(this,AdminPemilihan::class.java)
        startActivity(intent1)
    }

    fun goToWilayah(view: View) {
        intent1=Intent(this,AdminWilayah::class.java)
        startActivity(intent1)
    }

    fun goToPemilih(view: View) {
        intent1=Intent(this,AdminPemilih::class.java)
        startActivity(intent1)
    }
    fun goToJadwal(view: View) {
        intent1=Intent(this,AdminJadwal::class.java)
        startActivity(intent1)
    }
}