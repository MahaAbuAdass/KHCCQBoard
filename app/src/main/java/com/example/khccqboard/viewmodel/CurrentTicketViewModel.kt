package com.example.khccqboard.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khccqboard.data.CurrentTicket
import com.example.khccqboard.data.InValidTicket
import com.example.khccqboard.network.RetrofitBuilder

import retrofit2.HttpException

class CurrentTicketViewModel  (context: Context) : ViewModel() {

    private val retrofitBuilder = RetrofitBuilder(context)

    private val _currentTicket = MutableLiveData<CurrentTicket>()
    val currentTicket: LiveData<CurrentTicket> = _currentTicket

    private val _invalidTicket = MutableLiveData<InValidTicket?>() // For invalid tickets
    val invalidTicket: LiveData<InValidTicket?> = _invalidTicket

    private val _errorResponse = MutableLiveData<String?>()
    val errorResponse: LiveData<String?> = _errorResponse



  suspend fun getCurrentTicket(branchCode : String , displayNumber : String
  ){
            try {
                val response = retrofitBuilder.getCurrentTicket(branchCode
                    ,displayNumber
                )
                _currentTicket.postValue(response)
                _invalidTicket.postValue(null) // Clear any previous invalid ticket

            } catch (e: HttpException){
                handleHttpException(e)
            } catch (e: Exception) {
                // Handle other exceptions
                _errorResponse.postValue("Unexpected error occurred: ${e.message}")
            }
        }


    private fun handleHttpException(e: HttpException) {
        when (e.code()) {
            404 -> {
                // If 404, retain the previous `CurrentTicket` and update `_invalidTicket`
                val errorBody = e.response()?.errorBody()?.string() // Get the error body
                // Assuming you have a way to parse the error body into `InValidTicket`
                val invalidTicket = parseInvalidTicket(errorBody)
                _invalidTicket.postValue(invalidTicket)
            }
            else -> _errorResponse.postValue("HTTP error: ${e.code()} - ${e.message}")
        }
    }

    private fun parseInvalidTicket(errorBody: String?): InValidTicket? {
        // Parse the errorBody string to InValidTicket
        // You can use Gson or any JSON parsing library
        return errorBody?.let {
            // Return the parsed `InValidTicket`
            // Example: Gson().fromJson(it, InValidTicket::class.java)
            null // Replace with actual parsing logic
        }
    }

}