package com.example.mobappproject.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recycleViewIngredients.ArrayListAdapter
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapterTest
import com.example.mobappproject.recycleViewIngredients.SwipeCallback


class IngredientList : AppCompatActivity() {

    private var linearLayoutManager: LinearLayoutManager? = null
    var recyclerView: RecyclerView? = null
    val db = DatabaseHandler(this)
    private val mIngredients = ArrayList<DBIngredient>()
    private val availableIngredients = ArrayList<DBIngredient>()
    private var arrayListAdapter: ArrayListAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_list)

        loadIngredients()

        setUpInput()

        setRecycler()

    }

    /**
     * Sets up the AutoCompleteTextView. Adds onClickListener, setOnEditorActionListener to input.
     * Initializes the arrayListAdapter and adds it to input
     */
    private fun setUpInput() {
        val addInput = findViewById<Button>(R.id.addInput)
        addInput.setOnClickListener {
            addIngredient()
        }

        val input = findViewById<AutoCompleteTextView>(R.id.input)
        input.setOnEditorActionListener(TextView.OnEditorActionListener { view, keyCode, event ->
            var handled = false
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                addIngredient()
                handled = true
            } else if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
                addIngredient()
                handled = true
            }
            if (handled) {
                val focus = this.currentFocus
                if (focus != null) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focus.windowToken, 0)
                }
            }
            return@OnEditorActionListener handled
        })

        this.arrayListAdapter = ArrayListAdapter(this,
                android.R.layout.simple_dropdown_item_1line, availableIngredients)
        input.threshold = 1
        input.setAdapter(arrayListAdapter)
    }

    /**
     * Sets up the Recycler. setUpInput must be called before this to initialize the arrayListAdapter
     */
    private fun setRecycler() {
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapterTest(mIngredients, arrayListAdapter!!)
        recyclerView?.adapter = adapter
        val itemTouch = ItemTouchHelper(SwipeCallback(adapter))
        itemTouch.attachToRecyclerView(recyclerView)
    }

    /**
     * Called when the user wants to add an Ingredient to his List
     * Adds the ingredient to the user list and removes it from the availableList
     */
    private fun addIngredient(){
        val input = findViewById<AutoCompleteTextView>(R.id.input)
        val text = input.text.toString()
        if(text != "") {
            val fakeIng = DBIngredient(0,text,0,0)
            if(arrayListAdapter?.contains(fakeIng) == true) {
                val index = arrayListAdapter?.indexOf(fakeIng) as Int
                val ing = arrayListAdapter?.get(index) as DBIngredient
                if (db.addStoreIngredient(ing)) {
                    arrayListAdapter?.remove(ing)
                    arrayListAdapter?.notifyDataSetChanged()
                    ing.stored = 1
                    mIngredients.add(ing)

                    recyclerView?.adapter?.notifyDataSetChanged()
                    recyclerView?.scrollToPosition(mIngredients.size - 1)
                    val msg = Toast.makeText(this, text + " hinzugefügt", Toast.LENGTH_SHORT)
                    msg.show()
                    input.text.clear()
                } else {
                    val msg = Toast.makeText(this, text + " konnte nicht abgespeichert werden", Toast.LENGTH_SHORT)
                    msg.show()
                }
            } else if(mIngredients.contains(fakeIng)) {
                val msg = Toast.makeText(this, text + " ist bereits in deiner Liste", Toast.LENGTH_SHORT)
                msg.show()
            } else  {
                val msg = Toast.makeText(this, text + " ist keine valide Zutat", Toast.LENGTH_SHORT)
                msg.show()
            }
        }
    }

    /**
     * Loads all Ingredients from the database. Adds ingredients either to the users ingredientList
     * or to the available IngredientList
     */
    private fun loadIngredients() {
        val dbIngs = db.getIngredients()
        for (item in dbIngs){
            if(item.stored == 1 && !mIngredients.contains(item)) {
                    mIngredients.add(item)
            } else if(!availableIngredients.contains(item)){
                availableIngredients.add(item)
            }
        }
    }

}