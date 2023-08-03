package com.app.tasty.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import coil.load
import com.app.tasty.R
import com.app.tasty.baseClasses.BaseRecyclerViewAdapter
import com.app.tasty.databinding.ItemFoodCardLayoutBinding
import com.app.tasty.model.FoodItem
import com.app.tasty.utilities.RUPEE_SYMBOL
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ROW_TYPE_ITEM = 0

class FoodListAdapter(
    private val onClickRow: (Int) -> Unit,
    private val onClickAdd: (FoodItem) -> Unit,
    private val onClickReduce: (FoodItem) -> Unit,
    private val itemCount: (Int) -> Flow<Int?>
) : BaseRecyclerViewAdapter() {

    fun setUpData(foodItemList: List<FoodItem>) {
        notifyItemRangeRemoved(0, dataSet.size)
        dataSet.clear()
        foodItemList.forEach {
            dataSet.add(RowData(type = ROW_TYPE_ITEM, data = it))
        }
        notifyItemRangeInserted(0, dataSet.size)
    }

    override fun getRVRowLayout(rowType: Int): Int {
        return R.layout.item_food_card_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return FoodListViewHolder(
            rowView = LayoutInflater.from(parent.context)
                .inflate(getRVRowLayout(rowType = viewType), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindRowData(position = position, rowData = dataSet[position])
    }

    private inner class FoodListViewHolder(rowView: View) : BaseViewHolder(rowView = rowView) {
        @OptIn(DelicateCoroutinesApi::class)
        override fun bindRowData(position: Int, rowData: RowData) {
            val binding = ItemFoodCardLayoutBinding.bind(rowView)
            with(binding) {
                val foodItem: FoodItem? = dataSet[position].data as? FoodItem
                foodItem?.let {
                    rowView.setOnClickListener {
                        onClickRow(foodItem.id)
                    }
                    foodItemName.text = foodItem.name
                    foodItemPrice.text = RUPEE_SYMBOL.plus(foodItem.price.toString())
                    foodItemRating.text = foodItem.rating
                    foodItemRating.decideBgColorBasedOnRating(rating = foodItem.rating)
                    foodItemImage.load(foodItem.imageUrl)
                    GlobalScope.launch(Dispatchers.IO) {
                        itemCount(foodItem.id).catch {
                            Log.e("Error", "$it")
                        }.collect {
                            it?.let {
                                withContext(Dispatchers.Main) {
                                    itemCount.text = it.toString()
                                }
                                foodItem.quantity = it
                            }
                        }
                    }
                    addItem.setOnClickListener {
                        foodItem.quantity++
                        onClickAdd(foodItem)
                    }
                    reduceItem.setOnClickListener {
                        if (foodItem.quantity > 0) {
                            foodItem.quantity--
                            onClickReduce(foodItem)
                        }
                    }
                }
            }
        }
    }
}

private fun TextView.decideBgColorBasedOnRating(rating: String?) {
    rating?.let {
        val color = when (it.toDouble()) {
            in 4.0..5.0 -> {
                R.color.green
            }

            in 0.0..3.0 -> {
                R.color.red
            }

            else -> R.color.atomicTangerine
        }
        setBackgroundResource(color)
    }
}
