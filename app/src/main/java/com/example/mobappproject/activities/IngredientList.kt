package com.example.mobappproject.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapterTest


class IngredientList : AppCompatActivity() {

    private var linearLayoutManager: LinearLayoutManager? = null
    var recyclerView: RecyclerView? = null
    val db = DatabaseHandler(this)
    private val mIngredients = ArrayList<DBIngredient>()
    private val availableIngredients = ArrayList<DBIngredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_list)

        loadIngredients()

        setRecycler()

        setUpInput()


    }

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

        val adapter: ArrayAdapter<DBIngredient> = ArrayAdapter<DBIngredient>(this,
                android.R.layout.simple_dropdown_item_1line, availableIngredients)
        input.threshold = 1
        input.setAdapter(adapter)
    }

    private fun setRecycler() {
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapterTest(mIngredients)
        recyclerView?.adapter = adapter
        /*val itemTouch = ItemTouchHelper(SwipeCallback(adapter)
        itemTouch.attachToRecyclerView(recyclerView)*/
    }

    private fun addIngredient(){
        val input = findViewById<AutoCompleteTextView>(R.id.input)
        val text = input.text.toString()
        if(text != "") {
            val fakeIng = DBIngredient(0,text,0,0)
            if(availableIngredients.contains(fakeIng) ) {
                val index = availableIngredients.indexOf(fakeIng)
                val ing = availableIngredients[index]
                if (db.addStoreIngredient(ing)) {
                    availableIngredients.remove(ing)
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
            }
        }
    }


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