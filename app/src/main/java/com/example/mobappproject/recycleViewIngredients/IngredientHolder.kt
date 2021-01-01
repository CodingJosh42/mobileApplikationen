package com.example.mobappproject.recycleViewIngredients

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.database.DBIngredient

class IngredientHolder (inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_ingredient_list_item, parent, false)) {
    private var mTitleView: TextView? = null
    private var button: Button? = null


    init {
        mTitleView = itemView.findViewById<TextView>(R.id.name)
        button = itemView.findViewById<Button>(R.id.delete)
    }

    fun bind(ingredient: DBIngredient) {
        mTitleView?.text = ingredient.name
    }

    fun getButton(): Button? {
        return button
    }
}