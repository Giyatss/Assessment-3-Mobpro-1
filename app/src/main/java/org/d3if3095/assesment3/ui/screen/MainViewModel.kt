package org.d3if3095.assesment3.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if3095.assesment3.model.Mobil
import org.d3if3095.assesment3.network.MobilApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Mobil>())
        private set

    var status = MutableStateFlow(MobilApi.ApiStatus.LOADING)
        private  set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String) {
        viewModelScope.launch (Dispatchers.IO){
            status.value = MobilApi.ApiStatus.LOADING
            try {
                data.value = MobilApi.service.getMobil(userId)
                status.value = MobilApi.ApiStatus.SUCCESS
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = MobilApi.ApiStatus.FAILED
            }
        }
    }
    fun saveData(userId: String, merkMobil: String, tipeMobil: String, hargaMobil : String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MobilApi.service.postMobil(
                    userId,
                    merkMobil.toRequestBody("text/plain".toMediaTypeOrNull()),
                    tipeMobil.toRequestBody("text/plain".toMediaTypeOrNull()),
                    hargaMobil.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteData(userId: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = MobilApi.service.deleteMobil(userId, id)
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }
private fun Bitmap.toMultipartBody(): MultipartBody.Part {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 80, stream)
    val byteArray = stream.toByteArray()
    val requestBody = byteArray.toRequestBody(
        "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
    return MultipartBody.Part.createFormData(
        "image", "image.jpg", requestBody)
}
fun clearMessage() { errorMessage.value = null }
}

