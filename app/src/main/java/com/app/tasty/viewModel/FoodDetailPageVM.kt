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

class FoodDetailPageVM(private val mainRepositoryImpl: MainRepositoryImpl) : ViewModel() {

    private val _foodLiveData: MutableLiveData<FoodItem> by lazy {
        MutableLiveData<FoodItem>()
    }
    val foodLiveData: LiveData<FoodItem> = _foodLiveData

    private val _cartItemCount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val cartItemCount: LiveData<Int> = _cartItemCount

    fun getItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.getFoodItem(id = id).collect {
                _foodLiveData.postValue(it)
            }
        }
    }

    fun updateQuantityInFoodTable(quantity: Int, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.updateQuantityInFoodTable(quantity = quantity, id = id)
        }
    }

    fun insertToCart(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.insertCartItem(cartItem = cartItem)
        }
    }

    fun getItemCount(id: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            mainRepositoryImpl.getItemCount(id = id).collect {
                it?.let {
                    _cartItemCount.postValue(it)
                }
            }
        }
}