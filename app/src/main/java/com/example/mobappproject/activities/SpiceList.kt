package com.example.mobappproject.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter

class SpiceList : AppCompatActivity() {
    private var linearLayoutManager: LinearLayoutManager? = null
    var recyclerView: RecyclerView? = null
    private val spices = arrayListOf(
        Ingredient("Pfeffer"),
        Ingredient("Salz"),
        Ingredient("Zimt"),
        Ingredient("Nelken"),
        Ingredient("Chilli"),
        Ingredient("Paprikapulver"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spice_list)

        linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.adapter = RecyclerAdapter(spices)

        val addInput = findViewById<Button>(R.id.addInput)
        addInput.setOnClickListener {
            addSpice()
        }

        val input = findViewById<EditText>(R.id.input)
        input.setOnEditorActionListener(TextView.OnEditorActionListener  { view, keyCode, event ->
            var handled = false
            if (keyCode == EditorInfo.IME_ACTION_DONE ) {
                addSpice()
                handled = true
            } else if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
                addSpice()
                handled = true
            }
            if(handled){
                val focus = this.currentFocus
                if (focus != null) {
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focus.windowToken, 0)
                }
            }
            return@OnEditorActionListener handled
        })
    }

    private fun addSpice() {
        val input = findViewById<EditText>(R.id.input)
        if(input.text.toString() != "") {
            spices.add(Ingredient(input.text.toString()))
            recyclerView?.adapter?.notifyDataSetChanged()
        }

    }
}