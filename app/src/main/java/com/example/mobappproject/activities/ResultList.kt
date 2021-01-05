package com.example.mobappproject.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBRecipe
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recylcerResultList.RecyclerAdapterResult


class ResultList : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var recipes = ArrayList<DBRecipe>()
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.results)
        recyclerView?.layoutManager = linearLayoutManager

        recyclerView?.adapter = RecyclerAdapterResult(this, recipes)
        db = DatabaseHandler(this)
        addViews(loadRecipes())
    }

    private fun loadRecipes(): ArrayList<DBRecipe> {

        return db.getRecipes()

    }


    private fun addViews(recipeList: ArrayList<DBRecipe>) {
        for (recipe in recipeList){
            val imgId = this.resources.getIdentifier(recipe.picture, "drawable", this.packageName)
            recipe.imgId = imgId

            recipes.add(recipe)
            recyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    private fun filterRecipes(){
        
    }

}