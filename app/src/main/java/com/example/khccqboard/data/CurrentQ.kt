package com.example.khccqboard.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
data class CurrentQ (
    @SerialName("TicketNo"      ) var TicketNo      : String? = null,
    @SerialName("CounterId"     ) var CounterId     : Int?    = null,
    @SerialName("ServiceId"     ) var ServiceId     : Int?    = null,
    @SerialName("AlphaPrefix"   ) var AlphaPrefix   : String? = null,
    @SerialName("CounterDoorNo" ) var CounterDoorNo : String? = null,
    @SerialName("callTime"      ) var callTime      : String? = null


):Parcelable