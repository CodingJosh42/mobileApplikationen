package com.example.mobappproject.recycleViewIngredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.database.DBIngredient

class RecyclerAdapterTest(private val list: ArrayList<DBIngredient>) : RecyclerView.Adapter<IngredientHolderTest>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolderTest {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientHolderTest(inflater, parent)
    }

    override fun onBindViewHolder(holder: IngredientHolderTest, position: Int) {
        val ingredient: DBIngredient = list[position]
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