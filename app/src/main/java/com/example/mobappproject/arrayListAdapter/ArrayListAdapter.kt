package com.example.mobappproject.arrayListAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import android.R.id
import com.example.mobappproject.database.DBIngredient

/**
 * Contains and displays the available ingredients
 * @param context Fragment/Activity that uses this adapter
 * @param resource Resource id to display ingredients
 * @param objects List of available ingredients
 */
class ArrayListAdapter(context: FragmentActivity, resource: Int, objects: ArrayList<DBIngredient>) : ArrayAdapter<DBIngredient>(context, resource, objects) {
    var ingredients = objects
    var filteredIngredients = ingredients.clone() as ArrayList<DBIngredient>
    private var resId = resource
    private var filter = IngredientFilter(ingredients, this)

    override fun getPosition(item: DBIngredient?): Int {
        return filteredIngredients.indexOf(item)
    }

    override fun add(`object`: DBIngredient?) {
        if (`object` != null && !ingredients.contains(`object`)) {
            ingredients.add(`object`)
            filteredIngredients.add(`object`)
        }
    }

    override fun addAll(collection: MutableCollection<out DBIngredient>) {
        for (item in collection) {
            if (!ingredients.contains(item)) {
                ingredients.add(item)
                filteredIngredients.add(item)
            }
        }
    }

    override fun getCount(): Int {
        return filteredIngredients.size
    }

    override fun getItem(position: Int): DBIngredient? {
        return filteredIngredients[position]
    }

    override fun remove(`object`: DBIngredient?) {
        ingredients.remove(`object`)
        filteredIngredients.remove(`object`)
    }

    /**
     * Checks if ingredientList contains an ingredient
     * @param ingredient Ingredient to be checked
     */
    fun contains(ingredient: DBIngredient): Boolean {
        return ingredients.contains(ingredient)
    }

    /**
     * Returns index of an ingredient
     * @param ingredient ingredient to be checked
     */
    fun indexOf(ingredient: DBIngredient): Int {
        return ingredients.indexOf(ingredient)
    }

    /**
     * Returns an ingredient at a specific position
     * @param position position of ingredient
     */
    fun get(position: Int): DBIngredient {
        return ingredients[position]
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(resId, null)
        }

        val ingredient = filteredIngredients[position]
        val text = view?.findViewById<TextView>(id.text1)
        text?.text = ingredient.name

        return view as View
    }

    override fun getFilter(): Filter {
        return filter
    }
}