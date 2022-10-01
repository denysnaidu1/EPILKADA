package com.toxic.epilkada.com.toxic.epilkada

import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class DataPemilihUser {
    var agama:String=""
    var alamat:String=""
    var foto:ArrayList<String> = ArrayList()
    var gol_darah:String=""
    var jk:String=""
    var kab_kot:String=""
    var kecamatan:String=""
    var kel_desa:String=""
    var kewarganegaraan:String=""
    var nama:String=""
    var nik:String=""
    var pekerjaan:String=""
    var provinsi:String=""
    var rt_rw:String=""
    var status_kawin:String=""
    var status_pilbupkot:Boolean=false
    var status_pilgub:Boolean=false
    var tempat_lahir:String=""
    var tgl_lahir:Date?=null

    constructor(){

    }

    constructor(nik:String,nama:String,jk:String,foto:ArrayList<String>,rtRw:String,tglLahir:Date,tempat_lahir:String,statusPilgub:Boolean,statusPilbupkot:Boolean,statusKawin:String,
    kecamatan:String,kelDes:String,kabKot:String,provinsi:String,pekerjaan:String,negara:String,golDar:String,alamat:String,agama:String){
        this.nik=nik
        this.nama=nama
        this.jk=jk
        this.foto=foto
        this.rt_rw=rtRw
        this.tgl_lahir=tglLahir
        this.tempat_lahir=tempat_lahir
        this.status_pilgub=statusPilgub
        this.status_pilbupkot=statusPilbupkot
        this.status_kawin=statusKawin
        this.kecamatan=kecamatan
        this.kel_desa=kelDes
        this.kab_kot=kabKot
        this.provinsi=provinsi
        this.pekerjaan=pekerjaan
        this.kewarganegaraan=negara
        this.gol_darah=golDar
        this.alamat=alamat
        this.agama=agama
    }
}