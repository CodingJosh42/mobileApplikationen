package com.example.mobappproject.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Recipe
import com.example.mobappproject.recylcerResultList.RecyclerAdapterResult
import com.example.mobappproject.rest.RestDummy


class ResultList : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var recipes = ArrayList<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.results)
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.adapter = RecyclerAdapterResult(this, recipes)

        addViews(loadRecipes())
    }

    private fun loadRecipes(): List<Recipe> {
        val dummy = RestDummy()
        return dummy.getRecipes()
    }


    private fun addViews(recipeList: List<Recipe>) {
        for (recipe in recipeList) {

            val imgId = this.getResources().getIdentifier(recipe.img, "drawable", this.getPackageName())
            recipe.imgId = imgId

            recipes.add(recipe)
            recyclerView?.adapter?.notifyDataSetChanged()

        }
    }

    private fun filterRecipes(){
        
    }

}