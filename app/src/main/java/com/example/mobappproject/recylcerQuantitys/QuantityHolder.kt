package com.example.mobappproject.recylcerQuantitys

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBQuantity

/**
 * Holder class for quantity's
 * @param inflater Layout inflater
 * @param parent parent view
 */
class QuantityHolder (inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_ingredient_list_item, parent, false)) {
    private var name: TextView? = null
    private var button: Button? = null

    /**
     * Initializes name and button
     */
    init {
        name = itemView.findViewById(R.id.quantity_ing)
        button = itemView.findViewById(R.id.delete)
    }

    /**
     * Binds the quantity and ingredientName to the textView
     * @param quantity quantity to bind to textView
     */
    fun bind(quantity: DBQuantity) {
        val text = quantity.quantity + " " + quantity.ingredientName
        name?.text = text
    }

    /**
     * Returns the button
     */
    fun getButton(): Button? {
        return button
    }
}