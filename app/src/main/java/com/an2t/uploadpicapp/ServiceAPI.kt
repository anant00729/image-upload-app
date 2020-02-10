package com.an2t.uploadpicapp

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServiceAPI {


    //("http://beingdigitalz.co.in.md-in-50.webhostbox.net/apnaclinic-beta/api/patient/addMedicalReport.php")

    @Multipart
    @POST("addMedicalReport.php")
    fun fetchHomeOfferAPI(
        @Part image: MultipartBody.Part?,
        @Part("user_id") user_id: RequestBody,
        @Part("report_title") report_title: RequestBody,
        @Part("lab_name") lab_name: RequestBody,
        @Part("start_date") start_date: RequestBody,
        @Part("report_note") report_note: RequestBody
        /*, @Body RegParams mReg*/) : Call<Response<ServiceResponse>>





}