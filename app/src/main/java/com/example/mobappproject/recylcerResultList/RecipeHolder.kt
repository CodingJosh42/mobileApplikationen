package com.example.mobappproject.recylcerResultList


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.activities.ShowRecipe
import com.example.mobappproject.dataClasses.Recipe


class RecipeHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_result_ist_item, parent, false)) {
    private var title: TextView? = null
    private var img: ImageView? = null
    private var ingredients: TextView? = null
    private var matches: TextView? = null


    init {
        title = itemView.findViewById(R.id.title)
        img = itemView.findViewById(R.id.imageView)
        ingredients = itemView.findViewById(R.id.ingredients)
        matches = itemView.findViewById(R.id.matches)
    }

    fun bind(recipe: Recipe, context: Context) {
        title?.text = recipe.title
        title?.setOnClickListener {
            val intent = Intent(context, ShowRecipe::class.java)
            intent.putExtra("Id", recipe.id)
            context.startActivity(intent)
        }
        var ings = "Zutaten: "
        for(i in 0 until recipe.ingredients.size){
            ings += recipe.ingredients[i]
            if(i != recipe.ingredients.size-1) {
                ings += ", "
            }
        }
        ingredients?.text = ings

        var matchingIngs = "Matches: "
        for(i in 0 until recipe.matches.size){
            matchingIngs += recipe.matches[i]
            if(i != recipe.matches.size-1) {
                matchingIngs += ", "
            }
        }
        matches?.text = matchingIngs

        img?.setImageResource(recipe.imgId)
    }

}