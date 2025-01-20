package com.example.khccqboard.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentQ (
    @SerializedName("TicketNo"      ) var TicketNo      : String? = null,
    @SerializedName("CounterId"     ) var CounterId     : Int?    = null,

):Parcelable