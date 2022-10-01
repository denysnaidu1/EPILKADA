package com.toxic.epilkada

class DataWilayah {
    var id_wilayah:String=""
    var jlh_penduduk:Int=0
    var nama:String=""

    constructor(){

    }

    constructor(id:String,jlh:Int,wilayah:String){
        id_wilayah=id
        jlh_penduduk=jlh
        nama=wilayah
    }

}