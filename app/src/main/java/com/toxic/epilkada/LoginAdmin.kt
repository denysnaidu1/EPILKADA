package com.toxic.epilkada

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login_admin.*

class LoginAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)
    }

    fun submitAdmin(view: View) {
        if (userName.text.isNotEmpty() && password.text.isNotEmpty()) {
            val dialog = ProgressDialog(this)
            dialog.setMessage("Melakukan pengecekan...\nProses membutuhkan jaringan internet")
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            dialog.show()

            //Pengecekan NIK dan Password dengan tabel admin pada firebase
            FirebaseFirestore.getInstance().collection("admin")
                .whereEqualTo("nik_admin", userName.text.toString())
                .get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (a in it) {
                            if (a["password"]!!.toString().equals(password.text.toString())) {
                                dialog.dismiss()
                                val intent = Intent(this, AdminMain::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "NIK atau password salah.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                        }
                    } else {
                        dialog.dismiss()
                        Toast.makeText(this,"NIK atau password salah",Toast.LENGTH_SHORT).show()
                    }
                }

        } else {
            Toast.makeText(this, "Harap isi NIK dan password Anda!", Toast.LENGTH_SHORT).show()
        }

    }
}