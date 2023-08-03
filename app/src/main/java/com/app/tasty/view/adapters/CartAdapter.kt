package com.app.tasty.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tasty.R
import com.app.tasty.baseClasses.BaseRecyclerViewAdapter
import com.app.tasty.databinding.ItemCartOrderLayoutBinding
import com.app.tasty.model.CartItem
import com.app.tasty.utilities.RUPEE_SYMBOL

private const val ROW_TYPE_ITEM = 0

class CartAdapter(
    private val onCloseIconClick: (CartItem) -> Unit
) : BaseRecyclerViewAdapter() {

    fun setUpData(orderList: List<CartItem>) {
        notifyItemRangeRemoved(0, dataSet.size)
        dataSet.clear()
        orderList.forEach {
            dataSet.add(RowData(type = ROW_TYPE_ITEM, data = it))
        }
        notifyItemRangeInserted(0, dataSet.size)
    }

    override fun getRVRowLayout(rowType: Int): Int {
        return R.layout.item_cart_order_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OrderListViewHolder(
            rowView = LayoutInflater.from(parent.context)
                .inflate(getRVRowLayout(rowType = viewType), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindRowData(position = position, rowData = dataSet[position])
    }

    private inner class OrderListViewHolder(rowView: View) : BaseViewHolder(rowView = rowView) {
        override fun bindRowData(position: Int, rowData: RowData) {
            val binding = ItemCartOrderLayoutBinding.bind(rowView)
            with(binding) {
                val cartItem: CartItem? = dataSet[position].data as? CartItem
                cartItem?.let { item ->
                    foodItemName.text = item.name
                    "${item.quantity} X $RUPEE_SYMBOL${item.price}".also {
                        foodItemQuantityAndPrice.text = it
                    }
                    totalPrice.text = RUPEE_SYMBOL.plus(item.quantity * item.price)
                    closeIcon.setOnClickListener {
                        item.quantity = 0
                        onCloseIconClick(item)
                        notifyItemRemoved(item.id)
                    }
                }
            }
        }
    }
}