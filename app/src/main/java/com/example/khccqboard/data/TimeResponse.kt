package com.example.khccqboard.data

import com.google.gson.annotations.SerializedName

data class TimeResponse(
    @SerializedName("result"     ) var result    : String? = null,
    @SerializedName("error_code" ) var errorCode : Int?    = null,
    @SerializedName("msg_en"     ) var msgEn     : String? = null,
    @SerializedName("msg_ar"     ) var msgAr     : String? = null
)
