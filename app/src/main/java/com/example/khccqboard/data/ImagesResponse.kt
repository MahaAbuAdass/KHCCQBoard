package com.example.khccqboard.data

import com.google.gson.annotations.SerializedName

data class ImagesResponse(
    @SerializedName("logo_Client"  ) var logoClient  : String? = null,
    @SerializedName("logo_Default" ) var logoDefault : String? = null,
    @SerializedName("language" ) var language : String? = null,
    @SerializedName("Vidoe_Default" ) var vidoe_Default : String? = null,





)
