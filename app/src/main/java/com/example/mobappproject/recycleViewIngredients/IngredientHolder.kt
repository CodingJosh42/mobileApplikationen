package com.example.mobappproject.recycleViewIngredients

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient

/**
 * Holder Class for ingredients
 */
class IngredientHolder (inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_ingredient_list_item, parent, false)) {
    private var name: TextView? = null
    private var button: Button? = null

    /**
     * Initializes name and button
     */
    init {
        name = itemView.findViewById<TextView>(R.id.name)
        button = itemView.findViewById<Button>(R.id.delete)
    }

    /**
     * Binds the name of an ingredient to the textView (name)
     */
    fun bind(ingredient: DBIngredient) {
        name?.text = ingredient.name
    }

    /**
     * Returns the button
     */
    fun getButton(): Button? {
        return button
    }
}