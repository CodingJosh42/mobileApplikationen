package com.example.mobappproject.recycleViewIngredients
import android.widget.Filter
import com.example.mobappproject.database.DBIngredient
import java.util.*
import kotlin.collections.ArrayList

/**
 * Filters results for AutoCompleteTextView
 */
class IngredientFilter(ingredients: ArrayList<DBIngredient>, adapter: ArrayListAdapter) : Filter() {
    private var ingredients = ingredients
    private var adapter = adapter

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        if (constraint != null) {
            val suggestions: ArrayList<DBIngredient> = ArrayList()
            for (ingredient in ingredients) {
                if (ingredient.name.startsWith(constraint.toString(), ignoreCase = true)) {
                    suggestions.add(ingredient)
                }
            }
            results.values = suggestions
            results.count = suggestions.size
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.filteredIngredients.clear()
        if (results != null && results.count > 0) {
            val values: ArrayList<DBIngredient> = results.values as ArrayList<DBIngredient>
            adapter.filteredIngredients.addAll(values)
        }
        adapter.notifyDataSetChanged()
    }
}