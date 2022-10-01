package com.toxic.epilkada

class DataPemilihan {
    var id_paslon:String=""
    var jlh_suara:Int=0
    var nama_wilayah:String=""
    var id_pemilihan:String=""

    constructor(){

    }

    constructor(id:String,jlh:Int,wilayah:String,idPemilihan:String){
        id_paslon=id
        jlh_suara=jlh
        nama_wilayah=wilayah
        id_pemilihan=idPemilihan
    }

}