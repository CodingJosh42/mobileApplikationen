package com.example.mobappproject.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Recipe
import com.example.mobappproject.rest.RestDummy


class ResultList : AppCompatActivity() {

    private var inflater: LayoutInflater? = null
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)

        mContext = this
        inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val layout : LinearLayout  = findViewById(R.id.linearlayout1)

        addViews(layout, loadRecipes())
    }

    private fun loadRecipes(): List<Recipe> {
        val dummy = RestDummy()
        return dummy.getRecipes()
    }

    private fun showRecipe(view: View, id: Int) {
        val intent = Intent(this, ShowRecipe::class.java)
        intent.putExtra("Id", id)
        startActivity(intent)
    }

    private fun addViews(view: LinearLayout, recipes: List<Recipe>) {
        for (recipe in recipes) {
            val listItem = inflater?.inflate(R.layout.layout_resultList_item, null)

            val title = listItem?.findViewById(R.id.title) as TextView
            title.text = recipe.title

            listItem.id = recipe.id
            title.setOnClickListener {
                showRecipe(it, listItem.id)
            }

            val img = listItem.findViewById(R.id.imageView) as ImageView
            val imgId = this.getResources().getIdentifier(recipe.img, "drawable", this.getPackageName())
            img.setImageResource(imgId)

            val ingredients = listItem.findViewById(R.id.ingredients) as TextView
            var ings = "Zutaten: "
            for(i in 0 .. recipe.ingredients.size-1){
                ings += recipe.ingredients.get(i)
                if(i != recipe.ingredients.size-1) {
                    ings += ", "
                }
            }
            ingredients.text = ings

            val matches = listItem.findViewById(R.id.matches) as TextView
            var matchingIngs = "Matches: "
            for(i in 0 .. recipe.matches.size-1){
                matchingIngs += recipe.matches.get(i)
                if(i != recipe.matches.size-1) {
                    matchingIngs += ", "
                }
            }
            matches.text = matchingIngs

            view.addView(listItem)
        }
    }

}