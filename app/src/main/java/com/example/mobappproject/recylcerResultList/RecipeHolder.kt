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
    private var ingredientList: ArrayList<DBIngredient> ?= null
    private var matchingString = ""
    private var ingredientString = ""

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
    fun bind(recipe: DBRecipe, context: Context, ingredients: ArrayList<DBIngredient>?) {
        itemView.setOnClickListener {
            val intent = Intent(context, ShowRecipe::class.java)
            intent.putExtra("Id", recipe.id)
            context.startActivity(intent)
        }

        title?.text = recipe.name


        getIngredientText(recipe)


        this.ingredientList = ingredients

        getMatches()

        img?.setImageBitmap(recipe.picture)
    }

    /**
     * Binds ingredient names to ingredient textView
     */
    private fun getIngredientText(recipe: DBRecipe) {
        ingredientString = "Zutaten: "
        quantitys = recipe.quantitys
        for (i in 0 until quantitys!!.size) {
            ingredientString += if (i != quantitys!!.size - 1) {
                quantitys!![i].ingredientName + ", "
            } else {
                quantitys!![i].ingredientName
            }
        }
        this.ingredients?.text = ingredientString
    }

    /**
     * Binds matching ingredients to textView
     */
    private fun getMatches() {
        matchingString = ""
        if(ingredientList != null) {
            for (item in ingredientList!!) {
                if (contains(item)) {
                    matchingString += item.name + ", "
                }
            }
            if(matchingString.isNotEmpty()) {
                matchingString = "Matches: $matchingString"
                matchingString = matchingString.removeSuffix(", ")
                matches?.text = matchingString
            } else {
                val text = "Keine Matches"
                matches?.text = text
            }
        }
    }

    /**
     * Checks if quantity's contain an ingredient
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