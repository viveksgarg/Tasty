package com.app.tasty.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.app.tasty.R
import com.app.tasty.dataSource.cache.ApiCache
import com.app.tasty.dataSource.local.FoodDatabase
import com.app.tasty.databinding.FoodDetailPageBinding
import com.app.tasty.model.CartItem
import com.app.tasty.repository.MainRepositoryImpl
import com.app.tasty.utilities.RUPEE_SYMBOL
import com.app.tasty.viewModel.FoodDetailPageVM
import com.app.tasty.viewModel.vmFactory.TastyViewModelFactory

class FoodDetailPage : Fragment() {
    private var _binding: FoodDetailPageBinding? = null
    private val binding: FoodDetailPageBinding
        get() = _binding!!
    private val args: FoodDetailPageArgs by navArgs()
    private lateinit var viewModel: FoodDetailPageVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FoodDetailPageBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.let {
            it.show()
            it.setDisplayHomeAsUpEnabled(true)
            it.title = getString(R.string.app_name)
        }
        val dao = FoodDatabase.getDatabase(requireContext()).foodDao()
        val mainRepositoryImpl = MainRepositoryImpl(
            apiService = null, foodDAO = dao, context = requireContext(), cache = ApiCache
        )
        viewModel = ViewModelProvider(
            this, TastyViewModelFactory(mainRepositoryImpl)
        )[FoodDetailPageVM::class.java]
        viewModel.getItem(
            id = args.itemId.toInt()
        )
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.filterList)
        item.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityCompat.invalidateOptionsMenu(activity)
        registerObservers()
    }

    private fun registerObservers() {
        with(binding) {
            viewModel.foodLiveData.observe(
                viewLifecycleOwner
            ) { food ->
                food?.let { foodItem ->
                    viewModel.getItemCount(id = foodItem.id)
                    foodItemImage.load(foodItem.imageUrl)
                    foodItemName.text = foodItem.name
                    foodItemPrice.text = RUPEE_SYMBOL.plus(foodItem.price)
                    itemCount.text = foodItem.quantity.toString()
                    addItem.setOnClickListener { v ->
                        foodItem.quantity++
                        viewModel.updateQuantityInFoodTable(
                            quantity = foodItem.quantity, id = foodItem.id
                        )
                        viewModel.insertToCart(
                            cartItem = CartItem(
                                id = foodItem.id,
                                name = foodItem.name,
                                price = foodItem.price,
                                rating = foodItem.rating,
                                quantity = foodItem.quantity,
                                imageUrl = foodItem.imageUrl
                            )
                        )
                    }
                    reduceItem.setOnClickListener { v ->
                        if (foodItem.quantity > 0) {
                            foodItem.quantity--
                            viewModel.updateQuantityInFoodTable(
                                quantity = foodItem.quantity, id = foodItem.id
                            )
                            viewModel.insertToCart(
                                cartItem = CartItem(
                                    id = foodItem.id,
                                    name = foodItem.name,
                                    price = foodItem.price,
                                    rating = foodItem.rating,
                                    quantity = foodItem.quantity,
                                    imageUrl = foodItem.imageUrl
                                )
                            )
                        }
                    }
                    if (foodItem.quantity > 0) {
                        cart.cartLayout.visibility = View.VISIBLE
                        cart.quantityOfItems.text = foodItem.quantity.toString()
                        cart.foodItemPrice.text =
                            RUPEE_SYMBOL.plus(foodItem.quantity * foodItem.price).toString()
                    } else {
                        cart.cartLayout.visibility = View.GONE
                    }
                }
            }
            cart.viewCartCta.setOnClickListener {
                findNavController().navigate(
                    directions = FoodDetailPageDirections.actionFoodDetailPageToCartPage()
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}