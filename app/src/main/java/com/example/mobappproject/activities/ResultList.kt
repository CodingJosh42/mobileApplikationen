package com.example.mobappproject.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DBRecipe
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recylcerResultList.RecyclerAdapterResult

/**
 * ResultList activity. Displays a list of recipes
 */
class ResultList : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var recipes = ArrayList<DBRecipe>()
    private lateinit var db: DatabaseHandler
    private var ingredients: ArrayList<DBIngredient> = ArrayList()
    private var searchString: String = ""
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)

        val spices = ArrayList<DBIngredient>()
        bundle = intent.extras
        if (bundle != null) {
            ingredients = bundle!!.get("ingredients") as ArrayList<DBIngredient>
            searchString = bundle!!.get("searchString") as String
            spices.addAll(bundle!!.get("spices") as ArrayList<DBIngredient>)
        }

        db = DatabaseHandler(this)
        val recipes = if (spices.size > 0) {
            db.searchRecipes(ingredients, searchString, spices)
        } else {
            db.searchRecipes(ingredients, searchString, null)
        }
        if (recipes.size == 0) {
            val noResults = findViewById<TextView>(R.id.noResults)
            val text = "Keine Ergebnisse gefunden"
            noResults.text = text
        } else {
            val linearLayoutManager = LinearLayoutManager(this)
            recyclerView = findViewById(R.id.results)
            recyclerView?.layoutManager = linearLayoutManager

            addViews(recipes)
        }

    }

    /**
     * Loads ingredients of all recipes and adds them to recipeList
     * @param recipeList List of recipes that should load their ingredients and be added to recipes
     */
    private fun addViews(recipeList: ArrayList<DBRecipe>) {
        for (recipe in recipeList) {
            recipe.quantitys = db.getRecipeQuantities(recipe.id)
            recipes.add(recipe)
        }
        recipes.sortDescending()
        recyclerView?.adapter = RecyclerAdapterResult(this, recipes, ingredients)
    }
}