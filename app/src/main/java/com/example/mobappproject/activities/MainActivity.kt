package com.example.mobappproject.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.recyclerIngredientMain.RecyclerAdapterMain
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var inflater: LayoutInflater? = null
    private var mContext: Context? = null
    private var ingredientList = ArrayList<Ingredient>()
    private var catchPhraseList = ArrayList<Ingredient>()
    private var recyclerIngredients: RecyclerView? = null
    private var recyclerCatchphrase: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this
        inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerIngredients = findViewById(R.id.ingredients)
        recyclerIngredients?.layoutManager = linearLayoutManager
        recyclerIngredients?.adapter = RecyclerAdapterMain(ingredientList)

        val ingredientButton: FloatingActionButton = findViewById(R.id.ingredientButton)
        ingredientButton.setOnClickListener {
            addIngredient()
        }

        val linearLayoutManagerCatch = LinearLayoutManager(this)
        recyclerCatchphrase = findViewById(R.id.catchPhrases)
        recyclerCatchphrase?.layoutManager = linearLayoutManagerCatch
        recyclerCatchphrase?.adapter = RecyclerAdapterMain(catchPhraseList)

        val catchPhraseButton: FloatingActionButton = findViewById(R.id.catchPhraseButton)
        catchPhraseButton.setOnClickListener {
            addCatchPhrase()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.ingredientList -> {
                val intent = Intent(this, IngredientList::class.java)
                startActivity(intent)
                true
            }
            R.id.spiceList -> {
                val intent = Intent(this, SpiceList::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /** Called when the user taps the Search button */
    fun search(view: View) {
        val intent = Intent(this, ResultList::class.java)
        startActivity(intent)
    }

    private fun addIngredient() {
        val input: EditText = findViewById(R.id.inputIngredient)
        if(input.text.toString() != "") {
            ingredientList.add(Ingredient(input.text.toString()))
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(ingredientList.size -1)
        }
    }


    private fun addCatchPhrase() {
        val input: EditText = findViewById(R.id.inputCatchPhrase)
        if(input.text.toString() != "") {
            catchPhraseList.add(Ingredient(input.text.toString()))
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerCatchphrase?.scrollToPosition(catchPhraseList.size -1)
        }
    }

}