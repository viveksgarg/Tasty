package com.app.tasty.repository

import android.content.Context
import android.util.Log
import com.app.tasty.dataSource.cache.ApiCache
import com.app.tasty.dataSource.local.FoodDAO
import com.app.tasty.dataSource.network.ApiService
import com.app.tasty.model.CartItem
import com.app.tasty.model.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainRepositoryImpl(
    private val apiService: ApiService?,
    private val foodDAO: FoodDAO,
    private val context: Context,
    private val cache: ApiCache
) : MainRepository {

    override fun getFoodItemList(): Flow<List<FoodItem>> = flow {
        var model = cache.getApiResponse(context = context)
        if (model == null) {
            apiService?.fetchFoodItem()?.execute()?.body()?.let {
                model = it
            }
            model?.let { cache.saveApiResponse(context = context, responseList = it) }
        }
        model?.let { emit(value = it) }
    }

    override fun getFoodItem(id: Int): Flow<FoodItem> {
        return foodDAO.getFoodItem(id = id)
    }

    override fun insertFood(foodItem: FoodItem) {
        foodDAO.insertFood(foodItem = foodItem)
    }

    override fun getAllFoodItems(): Flow<List<FoodItem>> {
        return foodDAO.getAllFoodItems()
    }

    override fun getSumOfQuantity(): Flow<Int?> {
        return foodDAO.getSumOfQuantity()
    }

    override fun getSumOfOPrices(): Flow<Int?> {
        return foodDAO.getSumOfPrices()
    }

    override fun getItemCount(id: Int): Flow<Int?> {
        return foodDAO.getItemCount(id = id)
    }

    override fun deleteFoodItem(foodItem: FoodItem) {
        foodDAO.deleteFoodItem(foodItem = foodItem)
    }

    override fun updateQuantityInFoodTable(quantity: Int, id: Int) {
        Log.d("Vivek", "Not here")
        foodDAO.updateQuantityInFoodTable(quantity = quantity, id = id)
    }

    override fun insertCartItem(cartItem: CartItem) {
        foodDAO.insertCartItem(cartItem = cartItem)
    }

    override fun deleteCartItem(cartItem: CartItem) {
        foodDAO.deleteCartItem(cartItem = cartItem)
    }

    override fun getSumOfCartQuantity(): Flow<Int?> =
        foodDAO.getSumOfCartQuantity()

    override fun getSumOfCartPrices(): Flow<Int?> =
        foodDAO.getSumOfCartPrices()

    override fun getAllCartItems(): Flow<List<CartItem>> =
        foodDAO.getAllCartItems()
}














