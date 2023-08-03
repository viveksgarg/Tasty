package com.app.tasty.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
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
import com.app.tasty.databinding.CartPageBinding
import com.app.tasty.model.FoodItem
import com.app.tasty.repository.MainRepositoryImpl
import com.app.tasty.utilities.RUPEE_SYMBOL
import com.app.tasty.view.adapters.CartAdapter
import com.app.tasty.viewModel.CartPageVM
import com.app.tasty.viewModel.vmFactory.TastyViewModelFactory

class CartPage : Fragment() {
    private var _binding: CartPageBinding? = null
    private val binding: CartPageBinding
        get() = _binding!!
    private lateinit var viewModel: CartPageVM
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = CartPageBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.let {
            it.show()
            it.title = getString(R.string.cart)
            it.setDisplayHomeAsUpEnabled(true)
        }
        val dao = FoodDatabase.getDatabase(requireContext()).foodDao()
        val mainRepositoryImpl = MainRepositoryImpl(
            apiService = null, foodDAO = dao, context = requireContext(), cache = ApiCache
        )
        viewModel = ViewModelProvider(
            this, TastyViewModelFactory(mainRepositoryImpl)
        )[CartPageVM::class.java]
        binding.cartRecyclerView.apply {
            cartAdapter = CartAdapter {
                viewModel.insertFoodToTable(
                    foodItem = FoodItem(
                        id = it.id,
                        name = it.name,
                        price = it.price,
                        rating = it.rating,
                        quantity = it.quantity,
                        imageUrl = it.imageUrl
                    )
                )
                viewModel.updateCart(cartItem = it)
            }
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.foodLiveData.observe(
            viewLifecycleOwner
        ) { foodItems ->
            if (foodItems.isEmpty()) {
                findNavController().navigateUp()
            } else {
                cartAdapter.setUpData(
                    orderList = foodItems
                )
            }
        }
        viewModel.priceLd.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                binding.totalOrderPrice.text = RUPEE_SYMBOL.plus(it)
                binding.totalPaymentAmount.text = RUPEE_SYMBOL.plus(it + 30)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.filterList)
        item.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}