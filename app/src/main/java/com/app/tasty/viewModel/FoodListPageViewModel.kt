package com.app.tasty.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tasty.model.CartItem
import com.app.tasty.model.FoodItem
import com.app.tasty.repository.MainRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FoodListPageViewModel(private val mainRepositoryImpl: MainRepositoryImpl) : ViewModel() {

    init {
        getFoodItem()
        getSumOfPrices()
        getSumOfQuantity()
    }

    //Food Table
    private val _foodLiveData: MutableLiveData<List<FoodItem>> by lazy {
        MutableLiveData<List<FoodItem>>()
    }
    val foodLiveData: LiveData<List<FoodItem>> = _foodLiveData

    fun insertFoodToTable(foodItem: FoodItem) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.insertFood(foodItem = foodItem)
        }
    }

    private fun getFoodItem() = viewModelScope.launch(Dispatchers.IO) {
        mainRepositoryImpl.getFoodItemList().catch {
            Log.e("Error", "$it")
        }.collect {
            Log.d("FoodItemList", "$it")
            _foodLiveData.postValue(it)
        }
    }

    fun getItemCount(id: Int) = mainRepositoryImpl.getItemCount(id = id)

    //Cart Items
    fun insertCartItem(cartItem: CartItem) = viewModelScope.launch(Dispatchers.IO) {
        mainRepositoryImpl.insertCartItem(cartItem = cartItem)
    }

    private val _quantitySum: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val quantitySum: LiveData<Int> = _quantitySum

    private val _priceSum: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val priceSum: LiveData<Int> = _priceSum

    private fun getSumOfQuantity() = viewModelScope.launch(Dispatchers.IO) {
        mainRepositoryImpl.getSumOfCartQuantity().catch {
            Log.e("Error", "$it")
        }.collect {
            it?.let {
                _quantitySum.postValue(it)
            }
        }
    }

    private fun getSumOfPrices() = viewModelScope.launch(Dispatchers.IO) {
        mainRepositoryImpl.getSumOfCartPrices().catch {
            Log.e("Error", "$it")
        }.collect {
            it?.let {
                _priceSum.postValue(it)
            }
        }
    }
}