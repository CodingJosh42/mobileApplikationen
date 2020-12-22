package com.example.mobappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    }

    private fun addSpice() {
        val input = findViewById<EditText>(R.id.input)
        if(input.text.toString() != "") {
            spices.add(Ingredient(input.text.toString()))
            recyclerView?.adapter?.notifyDataSetChanged()
        }

    }
}