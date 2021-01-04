package com.example.mobappproject.recycleViewIngredients

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Swipe Callback for ingredientList. Removes ingredient from the recyclerView if swiped to the right
 */
class SwipeCallback : ItemTouchHelper.SimpleCallback{

    private var adapter: RecyclerAdapter? = null

    /**
     * @param adapter Adapter of the recyclerView to remove the item from the list
     */
   constructor(adapter: RecyclerAdapter): super(0, ItemTouchHelper.RIGHT)  {
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