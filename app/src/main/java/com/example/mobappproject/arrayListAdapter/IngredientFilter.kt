package com.example.mobappproject.arrayListAdapter
import android.widget.Filter
import com.example.mobappproject.database.DBIngredient
import kotlin.collections.ArrayList

/**
 * Filters results for AutoCompleteTextView
 * @param ingredients Contains all available ingredients
 * @param adapter Adapter that displays filtered list of ingredients
 */
class IngredientFilter(private var ingredients: ArrayList<DBIngredient>,private var adapter: ArrayListAdapter) : Filter() {

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