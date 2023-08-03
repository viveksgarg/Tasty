package com.app.tasty.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tasty.model.CartItem
import com.app.tasty.model.FoodItem
import com.app.tasty.repository.MainRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartPageVM(private val mainRepositoryImpl: MainRepositoryImpl) : ViewModel() {

    init {
        fetchAllOrders()
        getSumOfPrices()
    }

    private val _foodLiveData: MutableLiveData<List<CartItem>> = MutableLiveData<List<CartItem>>()
    val foodLiveData: LiveData<List<CartItem>> = _foodLiveData

    private val _priceLiveData: MutableLiveData<Int> = MutableLiveData<Int>()
    val priceLd: LiveData<Int> = _priceLiveData

    private fun fetchAllOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.getAllCartItems().collect {
                _foodLiveData.postValue(it)
            }
        }
    }

    private fun getSumOfPrices() = viewModelScope.launch(Dispatchers.IO) {
        mainRepositoryImpl.getSumOfCartPrices().collect {
            _priceLiveData.postValue(it)
        }
    }

    fun insertFoodToTable(foodItem: FoodItem) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.insertFood(foodItem = foodItem)
        }
    }

    fun updateCart(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.insertCartItem(cartItem = cartItem)
        }
    }
}