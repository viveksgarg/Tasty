package com.app.tasty.dataSource.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiEndpoints {

    const val base_url = "https://api.npoint.io/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

