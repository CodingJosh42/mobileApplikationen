package com.example.mobappproject.recycleViewIngredients

import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import com.example.mobappproject.database.DBIngredient

/**
 * Contains and displays the available ingredients
 * @param context Fragment that uses this adapter
 * @param resource Resource id to display ingredients
 * @param objects List of ingredients
 */
class ArrayListAdapter(context: FragmentActivity, resource: Int, objects: ArrayList<DBIngredient>) : ArrayAdapter<DBIngredient>(context, resource, objects) {
    private var ingredients = objects

    /**
     * Checks if the list of ingredients already contains the given ingredient. Returns true if yes
     */
    fun contains(ingredient: DBIngredient): Boolean {
        return ingredients.contains(ingredient)
    }

    /**
     * Returns index of the given ingredient
     */
    fun indexOf(ingredient: DBIngredient): Int {
        return ingredients.indexOf(ingredient)
    }

    /**
     * Returns the ingredient on the given position
     */
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
        for(item in collection) {
            if(!ingredients.contains(item)) {
                add(item)
            }
        }
    }

    /**
     * Removes all ingredients of the given list from the ingredientlist
     */
    fun removeAll(collection: MutableCollection<out DBIngredient>) {
        for(item in collection) {
            remove(item)
        }
    }

}