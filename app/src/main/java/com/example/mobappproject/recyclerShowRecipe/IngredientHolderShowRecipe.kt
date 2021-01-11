package com.example.mobappproject.recyclerShowRecipe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBQuantity

/**
 * Holder Class for Recipes
 * @param inflater Layout inflater
 * @param parent parent view
 */
class IngredientHolderShowRecipe(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_show_recipe_ingredients, parent, false)) {

    private var ingredient: TextView? = null
    private var quantity: TextView? = null


    /**
     * Initializes ingredient and quantity
     */
    init {
        ingredient = itemView.findViewById(R.id.quantity_ing)
        quantity = itemView.findViewById(R.id.quantity)
    }

    /**
     * Binds DBQuantity to textView
     * @param quantity Quantity to bind to textViews
     */
    fun bind(quantity: DBQuantity) {
        this.ingredient?.text = quantity.ingredientName
        this.quantity?.text = quantity.quantity

    }


}