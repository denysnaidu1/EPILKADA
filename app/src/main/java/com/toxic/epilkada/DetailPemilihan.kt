package com.toxic.epilkada


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail_pemilihan.*


class DetailPemilihan : Fragment() {

    private var jenis:List<String>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        val view = inflater.inflate(R.layout.fragment_detail_pemilihan, container, false)
        jenis = listOf(
            "Jenis pemilihan",
            "Pemilihan Gubernur",
            "Pemilihan Bupati/Wali Kota"
        )

        var choosen = ""
        var choosenProv = ""
        var choosenKabKot = ""
        var dataKabKot: ArrayList<String> = ArrayList()
        var dataMap: HashMap<String, String> = HashMap<String, String>()
        var dataProvinsi: ArrayList<String> = ArrayList()
        dataProvinsi.add("Provinsi")
        dataKabKot.add("Kota/Kabupaten")

        val dbProvinsi = FirebaseFirestore.getInstance().collection("wilayah").orderBy("nama")
        dbProvinsi.get().addOnSuccessListener {
                for (document in it) {
                    var temp = document.data["nama"].toString()
                    dataProvinsi.add(document.data["nama"].toString())
                    dataMap.put("ID_PROVINSI", document.id)
                    dataMap.put("NAMA_PROVINSI",temp)
                }
            }
        val btnLihatHasil=view.findViewById<Button>(R.id.btn_lihatHasil)
        val spinnerJenis = view.findViewById<Spinner>(R.id.spinnerJenis)
        val spinnerProvinsi = view.findViewById<Spinner>(R.id.spinnerProvinsi)
        val spinnerKabKot = view.findViewById<Spinner>(R.id.spinnerKabKot)

        spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
        spinnerProvinsi.setBackgroundResource(R.drawable.rectangle_inactive)

        spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosen = parent!!.getItemAtPosition(position).toString()
                if(choosen.equals(jenis!![0])){
                    spinnerKabKot.adapter = ArrayAdapter(
                        this@DetailPemilihan.context!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot
                    )
                    spinnerProvinsi.adapter = ArrayAdapter<String>(
                        this@DetailPemilihan.context!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataProvinsi
                    )
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle_inactive)
                }
                else if (choosen.equals(jenis!![1])) {
                    spinnerProvinsi.adapter = ArrayAdapter<String>(
                        this@DetailPemilihan.context!!,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi
                    )
                    spinnerKabKot.adapter = ArrayAdapter(
                        this@DetailPemilihan.context!!,
                        android.R.layout.simple_spinner_dropdown_item,
                        dataKabKot
                    )
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle_inactive)
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle)
                } else if (choosen.equals(jenis!![2])) {
                    spinnerProvinsi.setBackgroundResource(R.drawable.rectangle)
                    spinnerKabKot.setBackgroundResource(R.drawable.rectangle)
                    spinnerProvinsi.adapter = ArrayAdapter(
                        this@DetailPemilihan.context!!,
                        android.R.layout.simple_spinner_item,
                        dataProvinsi
                    )
                }
                Log.d("Choosen", choosen)
            }
        }

        spinnerProvinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosenProv = parent!!.getItemAtPosition(position).toString()
                if (choosen != "Pemilihan Gubernur" && choosen != "Jenis Pemilihan" && choosenProv != dataProvinsi[0]) {
                    val dbKabKot = FirebaseFirestore.getInstance().collection("wilayah")
                        .document(dataMap["ID_PROVINSI"]!!).collection("kabupaten_kota")
                        .orderBy("nama")
                    dbKabKot.get()
                        .addOnSuccessListener {
                            dataKabKot.clear()
                            dataKabKot.add("Kota/Kabupaten")
                            for (document in it) {
                                dataKabKot.add(document.data["nama"].toString())
                            }
                            spinnerKabKot.adapter = ArrayAdapter(
                                this@DetailPemilihan.context!!,
                                android.R.layout.simple_spinner_item,
                                dataKabKot
                            )
                        }
                }
                Log.d("ChoosenProv", choosenProv)
            }
        }

        spinnerKabKot.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                choosenKabKot = parent!!.getItemAtPosition(position).toString()
                Log.d("ChoosenKabkot", choosenKabKot)
            }
        }

        btnLihatHasil.setOnClickListener {
            if(choosen==jenis!![0]){
                Toast.makeText(this.context,"Harap isi data wilayah!",Toast.LENGTH_SHORT).show()
            }else {
                val fragmentHasil = FragmentHasil()
                val fragmentHasilManager = fragmentManager
                val fragmentTransaction = fragmentHasilManager!!.beginTransaction()
                val myBundle = Bundle()
                val dataBundle: ArrayList<String> = ArrayList()
                if (choosen == jenis!![1]) {
                    if (choosenProv != dataProvinsi[0]) {
                        dataBundle.add(dataMap["ID_PROVINSI"]!!)
                        dataBundle.add(dataMap["NAMA_PROVINSI"]!!)
                        myBundle.putStringArrayList("DATABUNDLE", dataBundle)
                        fragmentHasil.arguments = myBundle
                        fragmentTransaction.replace(
                            R.id.frameContainer,
                            fragmentHasil,
                            fragmentHasil.javaClass.simpleName
                        )
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    } else {
                        Toast.makeText(this.context, "Harap isi data provinsi!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (choosen == jenis!![2]) {
                    if (choosenProv != dataProvinsi[0] && choosenKabKot != dataKabKot[0]) {
                        dataBundle.add(dataMap["ID_PROVINSI"]!!)
                        dataBundle.add(dataMap["NAMA_PROVINSI"]!!)
                        dataBundle.add(choosenKabKot)
                        myBundle.putStringArrayList("DATABUNDLE", dataBundle)
                        fragmentHasil.arguments = myBundle
                        fragmentTransaction.replace(
                            R.id.frameContainer,
                            fragmentHasil,
                            fragmentHasil.javaClass.simpleName
                        )
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    } else {
                        Toast.makeText(
                            this.context,
                            "Harap isi data provinsi dan kabupaten/kota!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        spinnerJenis.adapter = ArrayAdapter(
            this.context!!,
            android.R.layout.simple_spinner_dropdown_item,
            jenis!!
        )
    }
}