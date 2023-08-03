package com.app.tasty.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tasty.R
import com.app.tasty.dataSource.cache.ApiCache
import com.app.tasty.dataSource.local.FoodDatabase
import com.app.tasty.dataSource.network.ApiEndpoints
import com.app.tasty.dataSource.network.ApiService
import com.app.tasty.databinding.FoodListPageBinding
import com.app.tasty.model.CartItem
import com.app.tasty.model.FoodItem
import com.app.tasty.repository.MainRepositoryImpl
import com.app.tasty.utilities.ConnectivityObserver
import com.app.tasty.utilities.NetworkConnectivityObserver
import com.app.tasty.utilities.RUPEE_SYMBOL
import com.app.tasty.view.adapters.FoodListAdapter
import com.app.tasty.viewModel.FoodListPageViewModel
import com.app.tasty.viewModel.vmFactory.TastyViewModelFactory

class FoodListPage : Fragment() {
    private var _binding: FoodListPageBinding? = null
    private val binding: FoodListPageBinding
        get() = _binding!!
    private lateinit var foodListAdapter: FoodListAdapter
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var viewModel: FoodListPageViewModel
    private var foodList = listOf<FoodItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FoodListPageBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.let {
            it.show()
            it.setDisplayHomeAsUpEnabled(true)
            it.title = getString(R.string.app_name)
        }
        setHasOptionsMenu(true)
        connectivityObserver = NetworkConnectivityObserver(requireContext())
        val apiService = ApiEndpoints.getInstance().create(ApiService::class.java)
        val dao = FoodDatabase.getDatabase(requireContext()).foodDao()
        val mainRepositoryImpl = MainRepositoryImpl(
            apiService = apiService, foodDAO = dao, context = requireContext(), cache = ApiCache
        )
        viewModel = ViewModelProvider(
            this, TastyViewModelFactory(mainRepositoryImpl)
        )[FoodListPageViewModel::class.java]
        binding.foodListRecyclerView.apply {
            foodListAdapter = FoodListAdapter(onClickRow = {
                findNavController().navigate(
                    directions = FoodListPageDirections.actionFoodListPageToFoodDetailPage(
                        itemId = it.toString()
                    )
                )
            }, onClickAdd = {
                viewModel.insertFoodToTable(foodItem = it)
                viewModel.insertCartItem(
                    cartItem = CartItem(
                        id = it.id,
                        name = it.name,
                        price = it.price,
                        rating = it.rating,
                        quantity = it.quantity,
                        imageUrl = it.imageUrl
                    )
                )
            }, onClickReduce = {
                viewModel.insertFoodToTable(foodItem = it)
                viewModel.insertCartItem(
                    cartItem = CartItem(
                        id = it.id,
                        name = it.name,
                        price = it.price,
                        rating = it.rating,
                        quantity = it.quantity,
                        imageUrl = it.imageUrl
                    )
                )
            }, itemCount = {
                viewModel.getItemCount(id = it)
            })
            adapter = foodListAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            viewModel.foodLiveData.observe(
                viewLifecycleOwner
            ) { items ->
                foodList = items
                foodListAdapter.setUpData(
                    foodItemList = foodList
                )
            }
            cartObserver()
            cart.viewCartCta.setOnClickListener {
                findNavController().navigate(
                    directions = FoodListPageDirections.actionFoodListPageToCartPage()
                )
            }
        }
    }

    private fun cartObserver() {
        with(binding) {
            viewModel.quantitySum.observe(
                viewLifecycleOwner
            ) {
                if (it > 0) {
                    cart.cartLayout.visibility = View.VISIBLE
                    cart.quantityOfItems.text = it.toString()
                } else {
                    cart.cartLayout.visibility = View.GONE
                }
            }
            viewModel.priceSum.observe(
                viewLifecycleOwner
            ) {
                Log.d("Vivek", "$it")
                cart.foodItemPrice.text = RUPEE_SYMBOL.plus(it.toString())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sortByRating -> {
                foodListAdapter.setUpData(foodItemList = foodList.sortedByDescending {
                    it.rating.toDouble()
                })
                true
            }

            R.id.sortByPrice -> {
                foodListAdapter.setUpData(foodItemList = foodList.sortedBy {
                    it.price
                })
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

