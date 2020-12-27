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
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var ingredientList = ArrayList<Ingredient>()
    private var recyclerIngredients: RecyclerView? = null
    private var userIngredientList = ArrayList<Ingredient>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set RecyclerView
        setRecyclerView()

        // Set onClick Listener for adding Ingredients and eventListener for enter
        setUpAddIngredient()

        // Set eventlistener (ENTER) for search input
        setUpSearch()

        // load ingredientlist from user
        getIngredientList()

        // Set onCheckedChangedListener to add and remove userIngredientList from ingredientList
        setUpSwitchButton()
    }

    /**
     * Sets up switch Button to add and remove userIngredientList from ingredientList
     */
    private fun setUpSwitchButton() {
        val useList = findViewById<SwitchCompat>(R.id.useList)
        useList.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                addUserList()
            } else {
                removeUserList()
            }
        }
    }

    /**
     * Add onKeyListener (ENTER) to EditText from search input
     */
    private fun setUpSearch() {
        val inputSearch: EditText = findViewById(R.id.inputSearch)
        inputSearch.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                search(view)
                val focus = this.currentFocus
                if (focus != null) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focus.windowToken, 0)
                }
                return@OnKeyListener true
            }
            false
        })
    }

    /**
     * Adds onClickListener to add Ingredient Button and onKeyListener (ENTER) to EditText
     * from add Ingredient input
     */
    private fun setUpAddIngredient() {
        val ingredientButton: FloatingActionButton = findViewById(R.id.ingredientButton)
        ingredientButton.setOnClickListener {
            addIngredient()
        }

        val inputIng: EditText = findViewById(R.id.inputIngredient)
        inputIng.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                addIngredient()
                return@OnKeyListener true
            }
            false
        })
    }

    /**
     * Sets up the RecylcerView of ingredients
     */
    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerIngredients = findViewById(R.id.ingredients)
        recyclerIngredients?.layoutManager = linearLayoutManager
        recyclerIngredients?.adapter = RecyclerAdapter(ingredientList)
    }

    /**
     * Creates OptionsMenu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Handles selected menu Item
     */
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


    /**
     * Starts new Activity (ResultList) and gives ingredients and catchPhrase to it
     * ResultList performs search with given information
     */
    fun search(view: View) {
        val inputSearch: EditText = findViewById(R.id.inputSearch)
        val text = inputSearch.text.toString()
        if(!(text == "" && ingredientList.size == 0)) {
            val intent = Intent(this, ResultList::class.java)
            if(text != "") {
                intent.putExtra("searchString", text)
            }
            intent.putExtra("ingredients", ingredientList)
            startActivity(intent)
        }
    }

    /**
     * Adds an ingredient to the list
     */
    private fun addIngredient() {
        val input: EditText = findViewById(R.id.inputIngredient)
        val text = input.text.toString()
        if(text != "" && !checkDoubles(text, ingredientList)) {
            ingredientList.add(Ingredient(text))
            input.text.clear()
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(ingredientList.size -1)
            val focus = this.currentFocus
            if (focus != null) {
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focus.windowToken, 0)
            }
        }
    }

    /**
     * Loads ingredientList from user
     */
    private fun getIngredientList() {
        userIngredientList.addAll(arrayListOf(
                Ingredient("Gurke"),
                Ingredient("Tomate"),
                Ingredient("Käse"),
                Ingredient("Hefe"),
                Ingredient("Mehl"),
                Ingredient("Butter"),
                Ingredient("Öl"),
                Ingredient("Milch"),
                Ingredient("Schinken"),
        ))
    }

    /**
     * Adds ingredients from userlist to ingredientList
     */
    private fun addUserList() {
        if(userIngredientList.size > 0) {
            for(ing in userIngredientList){
                if(!checkDoubles(ing.name, ingredientList)) {
                    ingredientList.add(ing)
                }
            }
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(ingredientList.size - 1)
        }
    }

    /**
     * Removes ingredients from userList to ingredientList
     */
    private fun removeUserList() {
        if(userIngredientList.size > 0){
            ingredientList.removeAll(userIngredientList)
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(0)
        }
    }

    /**
     * Checks if given list contains ingredient with given String
     */
    private fun checkDoubles(toAdd: String, list: ArrayList<Ingredient>): Boolean {
        return list.contains(Ingredient(toAdd))
    }

}