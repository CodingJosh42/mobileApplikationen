package com.example.mobappproject.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapterTest
import com.example.mobappproject.recycleViewIngredients.SwipeCallback

class IngredientList : AppCompatActivity() {

    private var linearLayoutManager: LinearLayoutManager? = null
    var recyclerView: RecyclerView? = null
    val db = DatabaseHandler(this)
    private val mIngredients = ArrayList<Ingredient>()



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

        val input = findViewById<EditText>(R.id.input)
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
    }

    private fun setRecycler() {
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapter(mIngredients)
        recyclerView?.adapter = adapter
        val itemTouch = ItemTouchHelper(SwipeCallback(adapter))
        itemTouch.attachToRecyclerView(recyclerView)
    }

    private fun addIngredient(){
        val input = findViewById<EditText>(R.id.input)
        val text = input.text.toString()
        if(text != "") {
            if(!checkDoubles(text)) {
                mIngredients.add(Ingredient(text))
                db.addIngredient(text)
                recyclerView?.adapter?.notifyDataSetChanged()
                recyclerView?.scrollToPosition(mIngredients.size - 1)
                val msg = Toast.makeText(this, text + " hinzugefügt", Toast.LENGTH_SHORT)
                msg.show()
                input.text.clear()
            } else {
                val msg = Toast.makeText(this, text + " ist bereits in der Liste", Toast.LENGTH_SHORT)
                msg.show()
            }
        }
    }

    private fun checkDoubles(toAdd: String): Boolean {
        return mIngredients.contains(Ingredient(toAdd))
    }

    private fun loadIngredients() {
        val dbIngs = db.getIngredients()
        for (item in dbIngs){
            val ingredient = Ingredient(item.name)
            if(!mIngredients.contains(ingredient)) {
                mIngredients.add(ingredient)
            }
        }
    }

}