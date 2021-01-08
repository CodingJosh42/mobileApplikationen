package com.example.mobappproject.recylcerResultList


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.activities.ShowRecipe
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DBQuantity
import com.example.mobappproject.database.DBRecipe

/**
 * Holder Class for Recipes
 */
class RecipeHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_result_list_item, parent, false)){
    private var title: TextView? = null
    private var img: ImageView? = null
    private var ingredients: TextView? = null
    private var matches: TextView? = null
    private var quantitys: ArrayList<DBQuantity> ?= null
    private var matchingString = ""

    /**
     * Initializes title, img, ingredients, matches
     */
    init {
        title = itemView.findViewById(R.id.title)
        img = itemView.findViewById(R.id.imageView)
        ingredients = itemView.findViewById(R.id.ingredients)
        matches = itemView.findViewById(R.id.matches)
    }


    /**
     * Binds recipe on result_list_item
     */
    fun bind(recipe: DBRecipe, context: Context) {
        itemView.setOnClickListener {
            val intent = Intent(context, ShowRecipe::class.java)
            intent.putExtra("Id", recipe.id)
            context.startActivity(intent)
        }

        title?.text = recipe.name

        var ings = "Zutaten: "
        quantitys = recipe.quantitys
        for (i in 0 until quantitys!!.size) {
            if (i != quantitys!!.size - 1) {
                ings += quantitys!![i].ingredientName + ", "
            } else {
                ings += quantitys!![i].ingredientName
            }
        }
        ingredients?.text = ings

        img?.setImageBitmap(recipe.picture)
    }

    /**
     * Binds matching ingredients to textView
     */
    fun getMatches(searchIngredients: ArrayList<DBIngredient>?) {
        if(searchIngredients != null) {
            for (item in searchIngredients) {
                if (contains(item)) {
                    matchingString += item.name + ", "
                }
            }
            if(matchingString.isNotEmpty()) {
                var matchingIngs = "Matches: $matchingString"
                matchingIngs = matchingIngs.removeSuffix(", ")
                matches?.text = matchingIngs
            } else {
                matches?.text = "Keine Matches"
            }
        }
    }

    /**
     * Checks if quantitys contain an ingredient
     */
    private fun contains(searchItem: DBIngredient): Boolean {
        for(item in quantitys!!) {
            if(item.ingredientName == searchItem.name) {
                return true
            }
        }
        return false
    }

}