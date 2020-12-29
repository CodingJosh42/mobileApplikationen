package com.example.mobappproject.recycleViewIngredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.dataClasses.Ingredient

class RecyclerAdapter(private val list: ArrayList<Ingredient>) : RecyclerView.Adapter<IngredientHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientHolder(inflater, parent)
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        if(position != 0) {
            notifyItemRangeChanged(position, list.size)
        } else {
            notifyDataSetChanged()
        }
    }
    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        val ingredient: Ingredient = list[position]
        holder.bind(ingredient)

        val button = holder.getButton()
        button?.setOnClickListener {
            removeItem(position)
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    fun getList(): ArrayList<Ingredient>  {
        return list
    }


}