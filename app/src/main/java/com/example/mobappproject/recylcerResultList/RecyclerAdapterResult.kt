package com.example.mobappproject.recylcerResultList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DBRecipe

/**
 * Displays Recipes from the list in a recyclerView
 * @param context Activity that uses this adapter
 * @param list List of recipes that should be displayed
 * @param ingredients List of ingredients from the user
 */
class RecyclerAdapterResult(private val context: Context, private val list: ArrayList<DBRecipe>, private val ingredients: ArrayList<DBIngredient>?) : RecyclerView.Adapter<RecipeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecipeHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        val recipe: DBRecipe = list[position]
        holder.bind(recipe, context)
        holder.getMatches(ingredients)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


