package com.example.mobappproject.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.recyclerIngredientMain.RecyclerAdapterMain
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var ingredientList = ArrayList<Ingredient>()
    private var catchPhraseList = ArrayList<Ingredient>()
    private var recyclerIngredients: RecyclerView? = null
    private var recyclerCatchphrase: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerIngredients = findViewById(R.id.ingredients)
        recyclerIngredients?.layoutManager = linearLayoutManager
        recyclerIngredients?.adapter = RecyclerAdapterMain(ingredientList)

        val ingredientButton: FloatingActionButton = findViewById(R.id.ingredientButton)
        ingredientButton.setOnClickListener {
            addIngredient()
        }
        val inputIng: EditText = findViewById(R.id.inputIngredient)
        inputIng.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                addIngredient()
                val focus = this.currentFocus
                if (focus != null) {
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focus.windowToken, 0)
                }
                return@OnKeyListener true
            }
            false
        })


        val linearLayoutManagerCatch = LinearLayoutManager(this)
        recyclerCatchphrase = findViewById(R.id.catchPhrases)
        recyclerCatchphrase?.layoutManager = linearLayoutManagerCatch
        recyclerCatchphrase?.adapter = RecyclerAdapterMain(catchPhraseList)

        val catchPhraseButton: FloatingActionButton = findViewById(R.id.catchPhraseButton)
        catchPhraseButton.setOnClickListener {
            addCatchPhrase()
        }
        val inputCatch: EditText = findViewById(R.id.inputCatchPhrase)
        inputCatch.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                addCatchPhrase()
                val focus = this.currentFocus
                if (focus != null) {
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focus.windowToken, 0)
                }
                return@OnKeyListener true
            }
            false
        })
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
        val text = input.text.toString()
        if(text != "" && !checkDoubles(text, ingredientList)) {
            ingredientList.add(Ingredient(text))
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(ingredientList.size - 1)
            input.text.clear()
        }
    }

    private fun checkDoubles(toAdd: String, list: ArrayList<Ingredient>): Boolean {
        return list.contains(Ingredient(toAdd))
    }


    private fun addCatchPhrase() {
        val input: EditText = findViewById(R.id.inputCatchPhrase)
        val text = input.text.toString()
        if(text != "" && !checkDoubles(text, catchPhraseList)) {
            catchPhraseList.add(Ingredient(text))
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerCatchphrase?.scrollToPosition(catchPhraseList.size - 1)
            input.text.clear()
        }
    }

}