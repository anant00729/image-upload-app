package com.an2t.uploadpicapp

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object Apifactory{
    var logging = HttpLoggingInterceptor()



    //OkhttpClient for building http request url
    private val client = OkHttpClient().newBuilder().addInterceptor(logging.setLevel(
        HttpLoggingInterceptor.Level.BODY))
        .build()

    fun retrofit() : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("http://beingdigitalz.co.in.md-in-50.webhostbox.net/apnaclinic-beta/api/patient/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val serviceAPI : ServiceAPI = retrofit().create(ServiceAPI::class.java)

}
