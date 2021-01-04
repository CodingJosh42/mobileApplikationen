package com.example.mobappproject.recyclerShowRecipe


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.activities.ShowRecipe
import com.example.mobappproject.dataClasses.Recipe
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DBQuantity

/**
 * Holder Class for Recipes
 */
class IngredientHolderShowRecipe(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_ingredient_show_recipe, parent, false)){

    private var ingredient: TextView? = null


    /**
     * Initializes ingredient
     */
    init {
        ingredient = itemView.findViewById(R.id.ingredient)
    }

    /**
     * Binds DBQuantity to textView
     */
    fun bind(quantity: DBQuantity) {
        val text = quantity.quantity + " " + quantity.ingredientName
        this.ingredient?.text = text

    }


}