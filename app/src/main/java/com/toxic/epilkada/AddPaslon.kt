package com.toxic.epilkada

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.MainThread
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.okhttp.Dispatcher
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_paslon.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class AddPaslon : AppCompatActivity() {
    val LOAD_IMAGE_RESULT=1
    var nomor=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_paslon)
        npNomor.maxValue=20
        npNomor.minValue=1
        npNomor.setOnValueChangedListener { numberPicker, i, i2 ->
            nomor=numberPicker.value
        }
    }

    fun addFoto(view: View) {
        var intentphoto=Intent(Intent.ACTION_PICK)
        intentphoto.setType("image/*")
        startActivityForResult(intentphoto,LOAD_IMAGE_RESULT)
    }

    var imageData:ByteArray?=null
    var bmp:Bitmap?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==LOAD_IMAGE_RESULT && resultCode== Activity.RESULT_OK && data!=null){
            val uri=data.data
            var filePath:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? =contentResolver.query(uri!!,filePath,null,null,null)
            cursor!!.moveToFirst()
            val imagePath=cursor.getString(cursor.getColumnIndex(filePath[0]))
            var path=File(imagePath)

            GlobalScope.launch (Dispatchers.Main){
                compress(path)
            }
        }
    }

    suspend fun compress(path: File) {
        val value=GlobalScope.async {
            bmp=Compressor(this@AddPaslon).compressToBitmap(path)
        }
        value.await()
        var imageByteArray:ByteArrayOutputStream= ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG,100,imageByteArray)
        imageData=imageByteArray.toByteArray()
        iv_temp.visibility=View.GONE
        ivFoto.setImageBitmap(bmp)
    }

    var newId=""
    var i=0
    var downloadUri: Uri?=null
    fun tambahPaslon(view: View) {
        if(edNIK1.text.isNotBlank() && edNIK2.text.isNotBlank() && edNama1.text.isNotBlank() && edNama2.text.isNotBlank() && edPartai.text.isNotBlank() && imageData!=null){
            val dialog = ProgressDialog(this@AddPaslon)
            dialog.setMessage("Menambahkan Data...\nProses membutuhkan jaringan internet")
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            dialog.show()
            FirebaseFirestore.getInstance().collection("paslon").orderBy("id_paslon", Query.Direction.DESCENDING).get()
                .addOnSuccessListener {
                    for (doc in it){
                        if(i==0){
                            newId=doc.id
                        }
                        i++
                    }
                    newId=(newId.toInt()+1).toString()
                    if(newId.length==2){
                        newId="0"+newId
                    }
                    else if(newId.length==1){
                        newId="00"+newId
                    }
                    val imageRef=FirebaseStorage.getInstance().getReferenceFromUrl("gs://epilkada.appspot.com/").child("paslon/"+newId)
                    imageRef.putBytes(imageData!!).continueWithTask {task->
                        if(!task.isSuccessful){
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener {
                        if(it.isSuccessful){
                            downloadUri=it.result
                            Log.d("Download",downloadUri.toString())
                            val dataBaru= hashMapOf(
                                "id_paslon" to newId,
                                "nama1" to edNama1.text.toString(),
                                "nama2" to edNama2.text.toString(),
                                "nik1" to edNIK1.text.toString(),
                                "nik2" to edNIK2.text.toString(),
                                "nomor" to nomor,
                                "partai" to edPartai.text.toString(),
                                "foto" to downloadUri.toString()
                            )
                            FirebaseFirestore.getInstance().collection("paslon").document(newId)
                                .set(dataBaru).addOnSuccessListener {
                                    dialog.dismiss()
                                    this.finish()
                                }
                        }
                    }
                }
        }else{
            Toast.makeText(this,"Harap isi semua form yang ada!",Toast.LENGTH_SHORT).show()
        }

    }
}