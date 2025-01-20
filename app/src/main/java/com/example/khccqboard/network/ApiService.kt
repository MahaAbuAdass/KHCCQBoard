package com.example.khccqboard.network


import com.example.khccqboard.data.CurrentQ
import com.example.khccqboard.data.CurrentTicket
import com.example.khccqboard.data.ScrollMessages
import com.example.khccqboard.data.TimeResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {


    @GET("api/AndriodGetScroll")
    suspend fun getScrollMessages(
        @Query("branchcode") branchcode: String
    ): ScrollMessages
//
//
    @GET("api/GetCurrentQAnd")
    suspend fun getCurrentQ(
        @Query("BranchCode") BranchCode: String
    ) : List<CurrentQ>

    @GET("api/AndriodGetCurrentCalled")
    suspend fun getCurrentTicket(
        @Query("branchcode") branchcode: String
      //  @Query("QBoardNo") QBoardNo: String

    ) : CurrentTicket
//
//    @GET("api/AndriodGetImages")
//    suspend fun getImages(
//        @Query("BaseURL") baseURL: String
//    ) : ImagesResponse


    @GET("api/Get_Current_Time")
    suspend fun getCurrentTime(
    ) : TimeResponse


//    @GET("api/GetVideoImageBoard")
//    suspend fun getImagesAndVideos(
//        @Query("BaseURL") baseURL: String
//    ) : List<FileURL>

}