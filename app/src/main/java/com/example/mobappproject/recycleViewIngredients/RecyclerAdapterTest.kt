package com.example.mobappproject.recycleViewIngredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.database.DBIngredient

class RecyclerAdapterTest(private val list: ArrayList<DBIngredient>, private val availableList: ArrayList<DBIngredient>) : RecyclerView.Adapter<IngredientHolderTest>() {

    private var ingredient: DBIngredient? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolderTest {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientHolderTest(inflater, parent)
    }

    override fun onBindViewHolder(holder: IngredientHolderTest, position: Int) {
        this.ingredient = list[position]
        holder.bind(ingredient!!)

        val button = holder.getButton()
        button?.setOnClickListener {
            remove(position)
        }
    }

    private fun remove(position: Int) {
        val ing = list[position]
        list.removeAt(position)
        if(position != 0)
            notifyItemRangeChanged(position, list.size)
        else
            notifyDataSetChanged()
        availableList.add(ing)
    }

    override fun getItemCount(): Int {
        return  list.size
    }


}