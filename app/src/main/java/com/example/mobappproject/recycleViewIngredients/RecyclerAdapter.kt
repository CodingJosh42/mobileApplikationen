package com.example.mobappproject.recycleViewIngredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.arrayListAdapter.ArrayListAdapter
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DatabaseHandler

/**
 * Displays ingredients of list in a recyclerView
 * @param list Contains ArrayList that should be displayed
 * @param adapter ArrayListAdapter that contains the available ingredients for the AutoCompleteTextView
 * @param db DatabaseHandler to remove the ingredients from the user list. Does nothing if null
 */
class RecyclerAdapter(private val list: ArrayList<DBIngredient>, private val adapter: ArrayListAdapter, private val db: DatabaseHandler?) : RecyclerView.Adapter<IngredientHolder>() {

    private var ingredient: DBIngredient? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        this.ingredient = list[position]
        holder.bind(ingredient!!)

        val button = holder.getButton()
        button?.setOnClickListener {
            remove(position)
        }
    }

    /**
     * Removes the ingredient from the recyclerView at the given position. Removes it also from the
     * user list if db is not null
     * @param position position of ingredient that should be removed
     */
    fun remove(position: Int) {
        val ing = list[position]
        var success = 1
        if (db != null) {
            success = db.removeStoreIngredient(ing)
        }
        if (success > -1) {
            list.removeAt(position)
            if (position != 0)
                notifyItemRangeChanged(position, list.size)
            else
                notifyDataSetChanged()
            adapter.add(ing)
            adapter.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}