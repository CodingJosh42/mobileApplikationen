package com.example.mobappproject.recyclerShowRecipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.database.DBQuantity

/**
 * Displays ingredients from the list in a recyclerView
 * @param list List of quantitys that should be displayed
 */
class RecyclerAdapterShowResult(private val list: ArrayList<DBQuantity>) : RecyclerView.Adapter<IngredientHolderShowRecipe>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolderShowRecipe {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientHolderShowRecipe(inflater, parent)
    }

    override fun onBindViewHolder(holder: IngredientHolderShowRecipe, position: Int) {
        val quantity: DBQuantity= list[position]
        holder.bind(quantity)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}