package com.app.tasty.repository

import com.app.tasty.model.CartItem
import com.app.tasty.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun getFoodItemList(): Flow<List<FoodItem>>

    fun insertFood(foodItem: FoodItem)

    fun getAllFoodItems(): Flow<List<FoodItem>>

    fun getFoodItem(id: Int): Flow<FoodItem>

    fun getSumOfQuantity(): Flow<Int?>

    fun getSumOfOPrices(): Flow<Int?>

    fun getItemCount(id: Int): Flow<Int?>

    fun deleteFoodItem(foodItem: FoodItem)

    fun updateQuantityInFoodTable(quantity: Int, id: Int)

    //Cart
    fun insertCartItem(cartItem: CartItem)
    fun deleteCartItem(cartItem: CartItem)
    fun getSumOfCartQuantity(): Flow<Int?>
    fun getSumOfCartPrices(): Flow<Int?>
    fun getAllCartItems(): Flow<List<CartItem>>
}