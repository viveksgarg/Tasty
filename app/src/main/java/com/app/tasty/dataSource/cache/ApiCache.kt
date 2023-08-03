package com.app.tasty.dataSource.cache

import android.content.Context
import androidx.core.content.edit
import com.app.tasty.model.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ApiCache {

    private const val PREFS_NAME = "api_cache"
    private const val KEY_API_RESPONSE = "api_response"

    private val gson: Gson = Gson()

    fun saveApiResponse(context: Context, responseList: List<FoodItem>) {
        val jsonString = gson.toJson(
            responseList
        )
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(
                KEY_API_RESPONSE, jsonString
            )
            apply()
        }
    }

    fun getApiResponse(context: Context): List<FoodItem>? {
        val prefs = context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        )
        val jsonString = prefs.getString(KEY_API_RESPONSE, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<FoodItem>>() {}.type
            gson.fromJson(
                jsonString, type
            )
        } else null
    }

    fun clearApiResponse(context: Context) {
        val prefs = context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        )
        prefs.edit {
            remove(KEY_API_RESPONSE)
            apply()
        }
    }
}