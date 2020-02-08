package com.an2t.uploadpicapp


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class ServiceResponse(
    val Id: String,
    val msg: String,
    val status: Int,
    val success: Boolean
)