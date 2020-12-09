package com.example.mobappproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    private var inflater: LayoutInflater? = null
    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this
        inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        var layout : LinearLayout  = findViewById(R.id.ingredients)

        addView(layout);

        var catchPhrases : LinearLayout  = findViewById(R.id.catchPhrases)

        addView(catchPhrases)
    }

    /** Called when the user taps the Search button */
    fun search(view: View) {
        val intent = Intent(this, ResultList::class.java)
        startActivity(intent)
    }

    fun addView(view: LinearLayout) {
        for (i in 0..9) {
            var ingredient = inflater?.inflate(R.layout.layout_ingredients, null)
            view.addView(ingredient)
        }
    }
}