package com.toxic.epilkada.com.toxic.epilkada

import com.google.firebase.Timestamp
import java.sql.Time
import java.util.*

class DataJadwal {
    var id_jadwal:String=""
    var id_wilayah:String=""
    var tgl_pelaksanaan:Timestamp?=null

    constructor(){

    }

    constructor(idjadwal:String,id:String,tgl:Timestamp){
        this.id_jadwal=idjadwal
        id_wilayah=id
        tgl_pelaksanaan=tgl
    }
}