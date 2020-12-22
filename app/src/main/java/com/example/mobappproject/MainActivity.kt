package com.example.mobappproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var inflater: LayoutInflater? = null
    private var mContext: Context? = null
    private var ingredientList = ArrayList<String>()
    private var ingCount = 0
    private var catchPhraseList = ArrayList<String>()
    private var catchCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this
        inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        val layout : LinearLayout  = findViewById(R.id.ingredients)
        val ingredientButton: FloatingActionButton = findViewById(R.id.ingredientButton)
        ingredientButton.setOnClickListener {
            addIngredient(layout)
        }

        val catchPhrases : LinearLayout  = findViewById(R.id.catchPhrases)
        val catchPhraseButton: FloatingActionButton = findViewById(R.id.catchPhraseButton)
        catchPhraseButton.setOnClickListener {
            addCatchPhrase(catchPhrases)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.ingredientList -> {
                val intent = Intent(this, IngredientList::class.java)
                startActivity(intent)
                true
            }
            R.id.spiceList -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /** Called when the user taps the Search button */
    fun search(view: View) {
        val intent = Intent(this, ResultList::class.java)
        startActivity(intent)
    }

    fun addIngredient(view: LinearLayout) {
        val text: EditText = findViewById(R.id.inputIngredient)
        if(text.getText().toString() != "") {
            val ingredient = inflater?.inflate(R.layout.layout_ingredients, null)
            if (ingredient != null) {
                ingredient.setId(ingCount)

                val newText : TextView = ingredient.findViewById(R.id.textView1)
                newText.setText(text.getText().toString())

                val imgButt : ImageButton = ingredient.findViewById(R.id.remove)
                imgButt.setOnClickListener{
                    removeIngredient(view, ingredient.getId())
                }

                ingredient.setTag("ingredient$ingCount")

                view.addView(ingredient)
                ingredientList.add(text.getText().toString())
                ingCount++
                System.out.println(ingredient.getTag().toString())
            }
            val scroll : ScrollView = findViewById(R.id.scrollview1)
            scroll.fullScroll(View.FOCUS_DOWN)

        }
    }

    fun removeIngredient(view: LinearLayout, id: Int) {
        System.out.println(id)
        val ing: ConstraintLayout = view.findViewWithTag("ingredient$id")
        val text : TextView = ing.findViewById(R.id.textView1)

        ingredientList.remove(text.getText().toString())
        view.removeView(ing)
    }

    fun addCatchPhrase(view: LinearLayout) {
        val text: EditText = findViewById(R.id.inputCatchPhrase)
        if(text.getText().toString() != "") {
            val catchPhrase = inflater?.inflate(R.layout.layout_ingredients, null)
            if (catchPhrase != null) {
                catchPhrase.setId(catchCount)

                val newText : TextView = catchPhrase.findViewById(R.id.textView1)
                newText.setText(text.getText().toString())

                val imgButt : ImageButton = catchPhrase.findViewById(R.id.remove)
                imgButt.setOnClickListener{
                    removeCatchPhrase(view, catchPhrase.getId())
                }

                catchPhrase.setTag("catchPhrase$catchCount")

                view.addView(catchPhrase)
                catchPhraseList.add(text.getText().toString())
                catchCount++
            }

            val scroll : ScrollView = findViewById(R.id.scrollview2)
            scroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun removeCatchPhrase(view: LinearLayout, id: Int) {
        val catch: ConstraintLayout = view.getRootView().findViewWithTag("catchPhrase$id")
        val text : TextView = catch.findViewById(R.id.textView1)

        catchPhraseList.remove(text.getText().toString())
        view.removeView(catch)
    }
}