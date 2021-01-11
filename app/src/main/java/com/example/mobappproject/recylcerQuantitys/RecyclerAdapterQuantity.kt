package com.example.mobappproject.recylcerQuantitys

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.database.DBQuantity
import com.example.mobappproject.arrayListAdapter.ArrayListAdapter

/**
 * RecyclerAdapter to display quantitys
 * @param list List of quantitys that should be displayed
 * @param adapter ArrayListAdapter of AutoCompleteTextView to add removed quantity to it
 */
class RecyclerAdapterQuantity(private val list: ArrayList<DBQuantity>, private val adapter: ArrayListAdapter) : RecyclerView.Adapter<QuantityHolder>() {
    private var quantity: DBQuantity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuantityHolder {
        val inflater = LayoutInflater.from(parent.context)
        return QuantityHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: QuantityHolder, position: Int) {
        this.quantity = list[position]
        holder.bind(quantity!!)

        val button = holder.getButton()
        button?.setOnClickListener {
            remove(position)
        }
    }

    /**
     * Removes the quantity from the recyclerView at the given position. Adds ingredient to ArrayList
     * Adapter
     * @param position position of quantity
     */
    fun remove(position: Int) {
        val quantity = list[position]
        list.removeAt(position)
        if (position != 0)
            notifyItemRangeChanged(position, list.size)
        else
            notifyDataSetChanged()
        adapter.add(quantity.ingredient)
        adapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}