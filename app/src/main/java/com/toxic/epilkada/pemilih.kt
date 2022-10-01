package com.toxic.epilkada

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class pemilih (
    val nik:String,
    val nama:String,
    val agama:String,
    val jk:String,
    val kab_kota:String,
    val provinsi:String,
    val tpt_lahir:String,
    val foto:ArrayList<String>,
    val status_pilbupkot:Boolean,
    val status_pilgub:Boolean,
    var jenisPilihan:String
):Parcelable