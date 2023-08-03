package com.app.tasty.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foodTable")
data class FoodItem(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Int,
    val rating: String,
    val imageUrl: String,
    var quantity: Int
)