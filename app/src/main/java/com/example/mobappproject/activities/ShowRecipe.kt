package com.example.mobappproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.database.DBQuantity
import com.example.mobappproject.database.DBRecipe
import com.example.mobappproject.recyclerShowRecipe.RecyclerAdapterShowResult

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
        img.setImageBitmap(recipe?.picture)

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
        val adapter = RecyclerAdapterShowResult(quantityList)
        recycler?.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}