package com.example.mobappproject.recyclerIngredientMain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.dataClasses.Ingredient

class RecyclerAdapterMain(private val list: ArrayList<Ingredient>) : RecyclerView.Adapter<IngredientHolderMain>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolderMain {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientHolderMain(inflater, parent)
    }

    override fun onBindViewHolder(holder: IngredientHolderMain, position: Int) {
        val ingredient: Ingredient = list[position]
        holder.bind(ingredient)

        val button = holder.getButton()
        button?.setOnClickListener {
            list.removeAt(position)
            if(position != 0)
                notifyItemRangeChanged(position, list.size)
            else
                notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}