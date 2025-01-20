package com.example.khccqboard.data

import com.google.gson.annotations.SerializedName

data class CurrentTicket(
      @SerializedName("TicketNo"      ) var TicketNo      : String? = null,
      @SerializedName("CounterId"     ) var CounterId     : String? = null,
      @SerializedName("ServiceId"     ) var ServiceId     : String? = null,
      @SerializedName("AlphaPrefix"   ) var AlphaPrefix   : String? = null,
      @SerializedName("CounterDoorNo" ) var CounterDoorNo : String? = null,
      @SerializedName("Path"          ) var Path          : String? = null,
      @SerializedName("QNameAr"       ) var QNameAr       : String? = null,
      @SerializedName("QNameEn"       ) var QNameEn       : String? = null
)



data class InValidTicket(
      @SerializedName("result"     ) var result    : String? = null,
      @SerializedName("error_code" ) var errorCode : Int?    = null,
      @SerializedName("msg_en"     ) var msgEn     : String? = null,
      @SerializedName("msg_ar"     ) var msgAr     : String? = null
)


