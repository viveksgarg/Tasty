package com.app.tasty.viewModel.vmFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.tasty.repository.MainRepositoryImpl
import com.app.tasty.viewModel.CartPageVM
import com.app.tasty.viewModel.FoodDetailPageVM
import com.app.tasty.viewModel.FoodListPageViewModel

class TastyViewModelFactory(private val mainRepositoryImpl: MainRepositoryImpl) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodListPageViewModel::class.java))
            return FoodListPageViewModel(mainRepositoryImpl) as T
        else if (modelClass.isAssignableFrom(FoodDetailPageVM::class.java))
            return FoodDetailPageVM(mainRepositoryImpl) as T
        else if (modelClass.isAssignableFrom(CartPageVM::class.java))
            return CartPageVM(mainRepositoryImpl) as T
        throw IllegalArgumentException("Unknown VM class")
    }
}