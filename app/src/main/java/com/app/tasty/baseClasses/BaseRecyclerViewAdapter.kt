package com.app.tasty.baseClasses

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter :
    RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder>() {

    /**
     * Data for each row
     */
    class RowData(val type: Int, val data: Any)

    protected val dataSet: MutableList<RowData> = ArrayList()

    /**
     * Initializes the views in rows
     */
    abstract class BaseViewHolder(protected var rowView: View) : RecyclerView.ViewHolder(rowView) {
        abstract fun bindRowData(position: Int, rowData: RowData)
    }

    /**
     * Get the layout id for each row type
     */
    protected abstract fun getRVRowLayout(rowType: Int): Int?

    override fun getItemCount(): Int = if (dataSet.isNullOrEmpty()) {
        0
    } else {
        dataSet.size
    }

    override fun getItemViewType(position: Int): Int = dataSet[position].type
}