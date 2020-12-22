package com.example.mobappproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter

class IngredientList : AppCompatActivity() {
    private var linearLayoutManager: LinearLayoutManager? = null
    var recyclerView: RecyclerView? = null
    private val mIngredients = arrayListOf(
        Ingredient("Gurke"),
        Ingredient("Tomate"),
        Ingredient("Käse"),
        Ingredient("Hefe"),
        Ingredient("Mehl"),
        Ingredient("Butter"),
        Ingredient("Öl"),
        Ingredient("Milch"),
        Ingredient("Schinken"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_list)

        linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.adapter = RecyclerAdapter(mIngredients)

        val addInput = findViewById<Button>(R.id.addInput)
        addInput.setOnClickListener {
            addIngredient()
        }
    }

    private fun addIngredient() {
        val input = findViewById<EditText>(R.id.input)
        if(input.text.toString() != "") {
            mIngredients.add(Ingredient(input.text.toString()))
            recyclerView?.adapter?.notifyDataSetChanged()
        }

    }


}