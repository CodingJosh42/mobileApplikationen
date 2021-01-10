package com.example.mobappproject.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DBRecipe
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recylcerResultList.RecyclerAdapterResult


class ResultList : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var recipes = ArrayList<DBRecipe>()
    private lateinit var db: DatabaseHandler
    private var ingredients: ArrayList<DBIngredient> = ArrayList()
    private var searchString: String = ""
    private var bundle: Bundle ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)

        bundle = intent.extras
        if (bundle != null) {
            ingredients = bundle!!.get("ingredients") as ArrayList<DBIngredient>
            searchString = bundle!!.get("searchString") as String
        }

        db = DatabaseHandler(this)
        val recipes = db.searchRecipes(ingredients, searchString)
        if(recipes.size == 0) {
            val noResults = findViewById<TextView>(R.id.noResults)
            noResults.text = "Keine Ergebnisse gefunden"
        } else {
            val linearLayoutManager = LinearLayoutManager(this)
            recyclerView = findViewById(R.id.results)
            recyclerView?.layoutManager = linearLayoutManager

            recyclerView?.adapter = RecyclerAdapterResult(this, recipes, ingredients)
            addViews(recipes)
        }

    }

    /**
     * Loads ingredients of all recipes and adds them to recipeList
     */
    private fun addViews(recipeList: ArrayList<DBRecipe>) {
        for (recipe in recipeList){
            recipe.quantitys = db.getRecipeQuantitys(recipe.id)
            recipes.add(recipe)
        }
        recipes.sortDescending()
        recyclerView?.adapter?.notifyDataSetChanged()
    }
}