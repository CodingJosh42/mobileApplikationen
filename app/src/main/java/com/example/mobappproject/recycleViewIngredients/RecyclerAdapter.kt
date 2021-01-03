package com.example.mobappproject.recycleViewIngredients

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DatabaseHandler

/**
 * @param list Contains ArrayList that should be displayed
 * @param adapter ArrayListAdapter that contains the available ingredients for the AutoCompleteTextView
 * @param db DatabseHanlder to remove the ingredients from the Userlist. Does noting if null
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

    fun remove(position: Int) {
        val ing = list[position]
        if(db != null) {
            db.removeStoreIngredient(ing)
        }
        list.removeAt(position)
        if(position != 0)
            notifyItemRangeChanged(position, list.size)
        else
            notifyDataSetChanged()
        adapter.add(ing)
        adapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return  list.size
    }


}