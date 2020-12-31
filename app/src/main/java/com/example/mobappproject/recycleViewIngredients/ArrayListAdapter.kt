package com.example.mobappproject.recycleViewIngredients

import android.content.Context
import android.widget.ArrayAdapter
import com.example.mobappproject.database.DBIngredient

class ArrayListAdapter(context: Context, resource: Int, objects: ArrayList<DBIngredient>) : ArrayAdapter<DBIngredient>(context, resource, objects) {
    private var ingredients = objects

    fun contains(ingredient: DBIngredient): Boolean {
        return ingredients.contains(ingredient)
    }


}