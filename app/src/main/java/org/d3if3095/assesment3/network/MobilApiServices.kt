package org.d3if3095.assesment3.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if3095.assesment3.model.Mobil
import org.d3if3095.assesment3.model.OpStatus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://unspoken.my.id/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MobilApiServices {
    @GET("mobil.php")
    suspend fun getMobil(
        @Header("Authorization") userId: String
    ): List<Mobil>

    @Multipart
    @POST("mobil.php")
    suspend fun postMobil(
        @Header("Authorization") userId: String,
        @Part("merkMobil") merkMobil: RequestBody,
        @Part("tipeMobil") tipeMobil: RequestBody,
        @Part("hargaMobil") hargaMobil: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @DELETE("mobil.php")
    suspend fun deleteMobil(
        @Header("Authorization") userId: String,
        @Query("id") id: Long
    ): OpStatus
}

object MobilApi {
    val service: MobilApiServices by lazy {
        retrofit.create(MobilApiServices::class.java)
    }

    fun getBanMobilUrl(imageId: String): String {
        return "${BASE_URL}image.php?id=$imageId"
    }
enum class ApiStatus { LOADING, SUCCESS, FAILED }

}