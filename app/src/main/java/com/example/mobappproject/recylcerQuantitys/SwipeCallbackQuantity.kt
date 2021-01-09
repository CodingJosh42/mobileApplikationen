package com.example.mobappproject.recylcerQuantitys

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter

class SwipeCallbackQuantity : ItemTouchHelper.SimpleCallback {
    private var adapter: RecyclerAdapterQuantity? = null

    /**
     * @param adapter Adapter of the recyclerView to remove the item from the list
     */
    constructor(adapter: RecyclerAdapterQuantity): super(0, ItemTouchHelper.RIGHT)  {
        this.adapter = adapter
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        adapter?.remove(pos)
    }
}