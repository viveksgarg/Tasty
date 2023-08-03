package com.app.tasty.dataSource.network

import com.app.tasty.model.FoodItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("988004defeff9e388b35")
    fun fetchFoodItem(): Call<List<FoodItem>>
}