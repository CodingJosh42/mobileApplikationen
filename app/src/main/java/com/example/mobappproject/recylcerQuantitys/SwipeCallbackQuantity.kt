package com.example.mobappproject.recylcerQuantitys

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter

/**
 * Swipe Callback for Quantity RecyclerAdapter
 * @param adapter Adapter of the recyclerView to remove the item from the list
 */
class SwipeCallbackQuantity(private val adapter: RecyclerAdapterQuantity) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        adapter.remove(pos)
    }
}