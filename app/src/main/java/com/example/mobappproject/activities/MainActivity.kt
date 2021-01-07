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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recycleViewIngredients.ArrayListAdapter
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter
import com.example.mobappproject.recycleViewIngredients.SwipeCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var ingredientList = ArrayList<DBIngredient>()
    private var recyclerIngredients: RecyclerView? = null
    private var userIngredientList = ArrayList<DBIngredient>()
    private val db = DatabaseHandler(this)
    private val availableIngredients = ArrayList<DBIngredient>()
    private var arrayListAdapter: ArrayListAdapter ?= null
    private val spiceList= ArrayList<DBIngredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadIngredients()

        // Set onClick Listener for adding Ingredients and eventListener for enter
        setUpAddIngredient()

        // Set RecyclerView
        setRecyclerView()

        // Set eventlistener (ENTER) for search input
        setUpSearch()

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

        val inputIng: AutoCompleteTextView = findViewById(R.id.inputIngredient)
        inputIng.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                addIngredient()
                return@OnKeyListener true
            }
            false
        })

        this.arrayListAdapter = ArrayListAdapter(this,
                R.layout.simple_dropdown_item_1line, availableIngredients)
        inputIng.threshold = 1
        inputIng.setAdapter(arrayListAdapter)
    }

    /**
     * Sets up the RecylcerView of ingredients. setUpAddIngredient must be called before this
     */
    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerIngredients = findViewById(R.id.ingredients)
        recyclerIngredients?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapter(ingredientList, arrayListAdapter!!, null)
        recyclerIngredients?.adapter = adapter
        val itemTouch = ItemTouchHelper(SwipeCallback(adapter))
        itemTouch.attachToRecyclerView(recyclerIngredients)
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
            //if(text != "") {
            intent.putExtra("searchString", text)
            //}
            if (ingredientList.isNotEmpty()){
                for(item in spiceList) {
                    if(!ingredientList.contains(item)) {
                        ingredientList.add(item)
                    }
                }
            }

            intent.putExtra("ingredients", ingredientList)
            startActivity(intent)
        } else {
            Toast.makeText(this,"Schlagwort oder Zutat eingeben", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Adds an ingredient to the list
     */
    private fun addIngredient() {
        val input: AutoCompleteTextView = findViewById(R.id.inputIngredient)
        val text = input.text.toString()
        val fakeIng = DBIngredient(0, text, 0, 0)
        if(text != "" && arrayListAdapter?.contains(fakeIng) == true && !ingredientList.contains(fakeIng)) {
            val index = arrayListAdapter?.indexOf(fakeIng) as Int
            val ing = arrayListAdapter?.get(index) as DBIngredient
            ingredientList.add(ing)
            arrayListAdapter?.remove(ing)
            arrayListAdapter?.notifyDataSetChanged()

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
     * Loads all ingredients. Saves ingredients either in userList or availabeList
     */
    private fun loadIngredients() {
        // All ingredients
        val dbIngs = db.getIngredients()
        availableIngredients.addAll(dbIngs)
        // Stored ingredients
        val userList = db.getIngredients(2)
        userIngredientList.addAll(userList)
        // Stored spices
        spiceList.addAll(db.getIngredients(3))
    }

    /**
     * Adds ingredients of userlist to ingredientList
     */
    private fun addUserList() {
        if(userIngredientList.size > 0) {
            for(ing in userIngredientList){
                if(arrayListAdapter?.contains(ing) == true) {
                    ingredientList.add(ing)
                    arrayListAdapter?.remove(ing)
                    arrayListAdapter?.notifyDataSetChanged()
                }
            }
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(ingredientList.size - 1)
        }
    }

    /**
     * Removes ingredients of userList from ingredientList
     */
    private fun removeUserList() {
        if(userIngredientList.size > 0){
            ingredientList.removeAll(userIngredientList)
            arrayListAdapter?.addAll(userIngredientList)
            arrayListAdapter?.notifyDataSetChanged()

            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(0)
        }
    }


}