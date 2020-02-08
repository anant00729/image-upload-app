package com.an2t.uploadpicapp

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServiceAPI {


    //("http://beingdigitalz.co.in.md-in-50.webhostbox.net/apnaclinic-beta/api/patient/addMedicalReport.php")

    @Multipart
    @POST("addMedicalReport.php")
    fun fetchHomeOfferAPI(
        @Part image: MultipartBody.Part?,
        @Part user_id: MultipartBody.Part?,
        @Part report_title: MultipartBody.Part?,
        @Part lab_name: MultipartBody.Part?,
        @Part start_date: MultipartBody.Part?,
        @Part report_note: MultipartBody.Part?
        /*, @Body RegParams mReg*/) : Call<Response<ServiceResponse>>





}