package com.example.khccqboard.network


import android.content.Context
import com.example.khccqboard.util.PreferenceManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitBuilder(context: Context) {

    //lazy: define heavy variable as lazy to execute it when call it only

    private val apiService: ApiService by lazy {
       val baseUrl = PreferenceManager.getBaseUrl(context)
     //  val baseUrl = "http://192.168.0.195/Api/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl?:"")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }


//
    suspend fun getScrollMessages(branchcode:String) =apiService.getScrollMessages(branchcode)
//
    suspend fun getCurrentQ(branchcode:String)=apiService.getCurrentQ(branchcode)

    suspend fun getCurrentTicket(branchcode:String , DisplayNo:String ) = apiService.getCurrentTicket(branchcode,DisplayNo )
//
    suspend fun getImages(baseUrl : String ,branchcode:String)=apiService.getImages(baseUrl,branchcode)

    suspend fun getCurrentTime()=apiService.getCurrentTime()

   // suspend fun getImagesAndVideos(baseUrl : String)=apiService.getImagesAndVideos(baseUrl)
}


