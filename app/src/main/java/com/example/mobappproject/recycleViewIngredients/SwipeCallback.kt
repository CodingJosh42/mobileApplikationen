package com.example.mobappproject.recycleViewIngredients

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeCallback : ItemTouchHelper.SimpleCallback{

    private var adapter: RecyclerAdapter? = null

   constructor(adapter: RecyclerAdapter): super(0, ItemTouchHelper.RIGHT)  {
       this.adapter = adapter
   }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        adapter?.removeItem(pos)
    }

}