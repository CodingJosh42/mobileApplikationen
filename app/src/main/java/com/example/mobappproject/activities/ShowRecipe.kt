package com.example.mobappproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Recipe
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.database.DBQuantity
import com.example.mobappproject.database.DBRecipe
import com.example.mobappproject.recyclerShowRecipe.RecyclerAdapterShowResult
import com.example.mobappproject.rest.RestDummy

/**
 * Displays a single recipe more detailed
 */
class ShowRecipe : AppCompatActivity() {


    private val db = DatabaseHandler(this)
    private var recycler: RecyclerView? = null
    private var quantityList = ArrayList<DBQuantity>()
    private var recipe: DBRecipe?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe)

        val bundle = intent.extras
        var id = -1
        if(bundle != null) {
            id = bundle.get("Id") as Int
        }
        if(id != -1){
            recipe = db.getRecipeByID(id)
            quantityList = db.getRecipeQuantitys(id)
            ///loadQuantitys()
            setRecyclerView()
            setContent()
        }
    }



    /**
     * Fills title, img and preparation with values of recipe
     */
    private fun setContent() {

        val title = findViewById<TextView>(R.id.title)
        title.text = recipe?.name

        val img = findViewById<ImageView>(R.id.imageView)
        val imgId = this.resources.getIdentifier(recipe?.picture, "drawable", this.packageName)
        img.setImageResource(imgId)

        val preparation = findViewById<TextView>(R.id.preparation)
        preparation.text = recipe?.description
    }

    /**
     * Sets up the RecylcerView of quanitys.
     */
    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        recycler = findViewById(R.id.ingredients)
        recycler?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapterShowResult(quantityList)//quantityList
        recycler?.adapter = adapter
    }

    /**
     * Loads all quantitys of the recipe
     */
    private fun loadQuantitys() {
        this.quantityList.addAll(arrayListOf(
                DBQuantity(0,0,"3kg","Mehl"),
                DBQuantity(0,0,"1L","Milch"),
                DBQuantity(0,0,"2","Tomaten"),
                DBQuantity(0,0,"1","Gurke"),
                DBQuantity(0,0,"1EL","Salz"),
                DBQuantity(0,0,"1EL","Salz"),
                DBQuantity(0,0,"1EL","Salz"),
                DBQuantity(0,0,"1EL","Salz"),
                DBQuantity(0,0,"1EL","Salz"),
                DBQuantity(0,0,"1EL","Salz"),
                DBQuantity(0,0,"1EL","Salz"),
                DBQuantity(0,0,"1EL","Salz"),

            ))
    }
}