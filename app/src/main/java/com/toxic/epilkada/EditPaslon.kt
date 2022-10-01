package com.toxic.epilkada

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_paslon.*
import kotlinx.android.synthetic.main.activity_edit_paslon.*
import kotlinx.android.synthetic.main.activity_edit_paslon.edNIK1
import kotlinx.android.synthetic.main.activity_edit_paslon.edNIK2
import kotlinx.android.synthetic.main.activity_edit_paslon.edNama1
import kotlinx.android.synthetic.main.activity_edit_paslon.edNama2
import kotlinx.android.synthetic.main.activity_edit_paslon.edPartai
import kotlinx.android.synthetic.main.activity_edit_paslon.ivFoto
import kotlinx.android.synthetic.main.activity_edit_paslon.npNomor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class EditPaslon : AppCompatActivity() {
    var id=""
    var dialog:ProgressDialog?=null
    var imageData:ByteArray?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_paslon)
        id=intent.getStringExtra("EXTRA_ID")
        dialog = ProgressDialog(this@EditPaslon)
        dialog!!.setMessage("Mempersiapkan Data...\nProses membutuhkan jaringan internet")
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog!!.show()
        loadData()
    }
    var bitmapDefault: Bitmap?=null
    var foto=""

    private fun loadData() {
        FirebaseFirestore.getInstance().collection("paslon").document(id)
            .get().addOnSuccessListener {
                edNama1.hint=it["nama1"].toString()
                edNama2.hint=it["nama2"].toString()
                edNIK1.hint=it["nik1"].toString()
                edNIK2.hint=it["nik2"].toString()
                edPartai.hint=it["partai"].toString()
                npNomor.hint=it["nomor"].toString()
                foto=it["foto"].toString()
                FirebaseStorage.getInstance().getReferenceFromUrl(foto)
                    .getBytes(Long.MAX_VALUE).addOnSuccessListener {
                        bitmapDefault = BitmapFactory.decodeByteArray(it, 0, it.size)
                        ivFoto.setImageBitmap(bitmapDefault)
                        dialog!!.dismiss()
                    }
            }
    }

    val LOAD_IMAGE_RESULT=1
    fun addFoto(view: View) {
        var intentphoto= Intent(Intent.ACTION_PICK)
        intentphoto.setType("image/*")
        startActivityForResult(intentphoto,LOAD_IMAGE_RESULT)
    }
    var status=false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==LOAD_IMAGE_RESULT && resultCode== Activity.RESULT_OK && data!=null) {
            status=true
            val uri = data.data
            var filePath: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = contentResolver.query(uri!!, filePath, null, null, null)
            cursor!!.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]))
            var path = File(imagePath)

            GlobalScope.launch(Dispatchers.Main) {
                compress(path)
            }
        }
    }

    suspend fun compress(path: File) {
        var bmp:Bitmap?=null
        val value=GlobalScope.async {
            bmp= Compressor(this@EditPaslon).compressToBitmap(path)
        }
        value.await()
        val imageByteArray: ByteArrayOutputStream = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG,100,imageByteArray)
        imageData=imageByteArray.toByteArray()
        ivFoto.setImageBitmap(bmp)
    }

    fun tambahPaslon(view: View) {
        dialog = ProgressDialog(this@EditPaslon)
        dialog!!.setMessage("Menyimpan Data...\nProses membutuhkan jaringan internet")
        dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog!!.show()
        var textNama1=""
        var textNama2=""
        var nik1=""
        var nik2=""
        var partai=""
        var nomor=""
        if(edNama1.text.isNotBlank()){
            textNama1=edNama1.text.toString()
        }else{
            textNama1=edNama1.hint.toString()
        }
        if(edNama2.text.isNotBlank()){
            textNama2=edNama2.text.toString()
        }else{
            textNama2=edNama2.hint.toString()
        }
        if(edNIK1.text.isNotBlank()){
            nik1=edNIK1.text.toString()
        }else{
            nik1=edNIK1.hint.toString()
        }
        if(edNIK2.text.isNotBlank()){
            nik2=edNIK2.text.toString()
        }else{
            nik2=edNIK2.hint.toString()
        }
        if(edPartai.text.isNotBlank()){
            partai=edPartai.text.toString()
        }else{
            partai=edPartai.hint.toString()
        }
        if(npNomor.text.isNotBlank()){
            nomor=npNomor.text.toString()
        }else{
            nomor=npNomor.hint.toString()
        }
        if (status){
            val imageRef=FirebaseStorage.getInstance().getReferenceFromUrl("gs://epilkada.appspot.com/").child("paslon/"+id)
            imageRef.putBytes(imageData!!).continueWithTask {task->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    val downloadUri=it.result
                    Log.d("Download",downloadUri.toString())
                    val dataBaru= hashMapOf(
                        "id_paslon" to id,
                        "nama1" to textNama1,
                        "nama2" to textNama2,
                        "nik1" to nik1,
                        "nik2" to nik2,
                        "nomor" to nomor.toInt(),
                        "partai" to partai,
                        "foto" to downloadUri.toString()
                    )
                    FirebaseFirestore.getInstance().collection("paslon").document(id)
                        .set(dataBaru).addOnSuccessListener {
                            dialog!!.dismiss()
                            this.finish()
                        }
                }
            }
        }else{
            val dataBaru= hashMapOf(
                "id_paslon" to id,
                "nama1" to textNama1,
                "nama2" to textNama2,
                "nik1" to nik1,
                "nik2" to nik2,
                "nomor" to nomor.toInt(),
                "partai" to partai,
                "foto" to foto
            )
            FirebaseFirestore.getInstance().collection("paslon").document(id)
                .set(dataBaru).addOnSuccessListener {
                    dialog!!.dismiss()
                    this.finish()
                }
        }
    }
}