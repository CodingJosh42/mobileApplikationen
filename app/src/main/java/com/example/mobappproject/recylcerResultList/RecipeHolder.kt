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

        val ings = "Zutaten: test"

        ingredients?.text = ings

        val matchingIngs = "Matches: "

        matches?.text = matchingIngs

        img?.setImageResource(recipe.imgId as Int)
    }


}