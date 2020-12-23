package com.example.mobappproject.recylcerResultList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.dataClasses.Recipe


class RecylcerAdapterResult(private val context: Context, private val list: ArrayList<Recipe>) : RecyclerView.Adapter<RecipeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecipeHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        val recipe: Recipe = list[position]
        holder.bind(recipe, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}