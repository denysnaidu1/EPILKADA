package com.toxic.epilkada.com.toxic.epilkada

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class fotoPemilih(
    val listFoto:ArrayList<Bitmap>,
    val index:ArrayList<Int>
):Parcelable