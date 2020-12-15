package com.example.mobappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class ShowRecipe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe)

        val bundle = getIntent().getExtras()
        var id = -1
        if(bundle != null) {
            id = bundle.get("Id") as Int
        }
        if(id != -1){
            setContent(id)
        }
    }

    private fun loadRecipe(id: Int): Recipe{
        val dummy = RestDummy()
        return dummy.getRecipe(id)
    }

    private fun setContent(id: Int) {
        val recipe = loadRecipe(id)

        val title = findViewById<TextView>(R.id.title)
        title.text = recipe.title

        val img = findViewById<ImageView>(R.id.imageView)
        val imgId = this.getResources().getIdentifier(recipe.img, "drawable", this.getPackageName())
        img.setImageResource(imgId)

        val ingredients = findViewById<TextView>(R.id.ingredients)
        var ings = "Zutaten: "
        for(i in 0 .. recipe.ingredients.size-1){
            ings += recipe.ingredients.get(i)
            if(i != recipe.ingredients.size-1) {
                ings += ", "
            }
        }
        ingredients.text = ings

        val preparation = findViewById<TextView>(R.id.preparation)
        preparation.text = "Zubereitung:\n" + recipe.preparation
    }
}