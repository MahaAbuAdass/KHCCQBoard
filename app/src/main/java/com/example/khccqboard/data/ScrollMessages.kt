package com.example.khccqboard.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScrollMessages(
    @SerializedName("ScrollMessageEn"       ) var ScrollMessageEn       : String? = null,
    @SerializedName("ScrollMessageAr"       ) var ScrollMessageAr       : String? = null,
    @SerializedName("ScrollBackgroungColor" ) var ScrollBackgroungColor : String? = null,
    @SerializedName("ScrollForeColor"       ) var ScrollForeColor       : String? = null
) : Parcelable
