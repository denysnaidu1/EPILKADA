package com.toxic.epilkada

class DataPaslon {
    var id_paslon:String=""
    var nama1:String=""
    var nama2:String=""
    var nik1:String=""
    var nik2:String=""
    var foto:String=""
    var nomor:Int=0
    var partai:String=""

    constructor(){

    }

    constructor(id:String,nama1:String,nama2:String,nik1:String,nik2:String,foto:String,nomor:Int,partai:String){
        id_paslon=id
        this.nama1=nama1
        this.nama2=nama2
        this.nik1=nik1
        this.nik2=nik2
        this.foto=foto
        this.nomor=nomor
        this.partai=partai
    }
}