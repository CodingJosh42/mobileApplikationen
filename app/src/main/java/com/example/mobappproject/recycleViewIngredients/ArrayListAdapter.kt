package com.example.mobappproject.recycleViewIngredients

import android.content.Context
import android.widget.ArrayAdapter
import com.example.mobappproject.database.DBIngredient

class ArrayListAdapter(context: Context, resource: Int, objects: ArrayList<DBIngredient>) : ArrayAdapter<DBIngredient>(context, resource, objects) {
    private var ingredients = objects

    fun contains(ingredient: DBIngredient): Boolean {
        return ingredients.contains(ingredient)
    }

    fun indexOf(ingredient: DBIngredient): Int {
        return ingredients.indexOf(ingredient)
    }

    fun get(position: Int): DBIngredient{
        return ingredients[position]
    }

    override fun remove(`object`: DBIngredient?) {
        super.remove(`object`)
        ingredients.remove(`object`)
    }

    override fun add(`object`: DBIngredient?) {
        super.add(`object`)
        if (`object` != null) {
            ingredients.add(`object`)
        }
    }

    override fun addAll(collection: MutableCollection<out DBIngredient>) {
        super.addAll(collection)
        ingredients.addAll(collection)
    }

    fun removeAll(collection: MutableCollection<out DBIngredient>) {
        ingredients.removeAll(collection)
        for(item in collection) {
            super.remove(item)
        }
    }

}