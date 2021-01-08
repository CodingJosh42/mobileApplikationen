package com.example.mobappproject.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DBQuantity
import com.example.mobappproject.database.DBRecipe
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.recycleViewIngredients.ArrayListAdapter
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter
import com.example.mobappproject.recycleViewIngredients.SwipeCallback
import java.io.ByteArrayOutputStream
import kotlin.math.ceil


class AddRecipe : AppCompatActivity() {

    private var ingredients = ArrayList<DBIngredient>()
    private var availableIngredients = ArrayList<DBIngredient>()
    private var db = DatabaseHandler(this)
    private var recyclerIngredients: RecyclerView ?= null
    private var arrayListAdapter: ArrayListAdapter ?= null
    private val ADD_RECIPE_IMAGE = 1
    private var imgButton: ImageButton ?= null
    private var imageBitmap: Bitmap ?= null
    private var placeholder: Bitmap ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)
        imgButton = findViewById(R.id.imageInput)

        loadIngredients()

        setUpAddIngredient()

        setRecyclerView()

        setUpImageInput()

        setUpSubmit()
    }

    private fun loadIngredients() {
        val dbIngs = db.getIngredients()
        availableIngredients.addAll(dbIngs)
    }

    private fun setUpSubmit() {
        val submit = findViewById<Button>(R.id.submit)
        submit.setOnClickListener {
            submit()
        }
    }

    /**
     * Sets up ImageInputButton
     */
    private fun setUpImageInput() {
        imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder)
        placeholder = imageBitmap
        imgButton?.setImageBitmap(imageBitmap)
        imgButton?.setOnClickListener {
            startGallery()
        }
    }

    /**
     * Starts Gallery activity
     */
    private fun startGallery() {
        val intent = Intent()
        intent.type = "image/*";
        intent.action = Intent.ACTION_GET_CONTENT;
        startActivityForResult(intent, ADD_RECIPE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int,  resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_RECIPE_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data as Uri;
            imgButton?.setImageURI(selectedImage)
            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
        }
    }

    /**
     * Loads image as Bitmap. Compresses Image so its not too big for the Database
     * @param uri Path of file
     */
    private fun getImage(uri: Uri): Bitmap {
        // Get Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        val byteArray =  stream.toByteArray()

        // Check size
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(byteArray,0,byteArray.size, options)
        val height = 300
        val width = 300
        val heightRatio = ceil(((options.outHeight / height.toFloat()).toDouble()))
        val widthRatio = ceil((options.outWidth / width.toFloat()).toDouble())

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio.toInt()
            } else {
                options.inSampleSize = widthRatio.toInt()
            }
        }

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size, options)
    }

    /**
     * Sets up the RecylcerView of ingredients. setUpAddIngredient must be called before this
     */
    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerIngredients = findViewById(R.id.ingredients)
        recyclerIngredients?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapter(ingredients, arrayListAdapter!!, null)
        recyclerIngredients?.adapter = adapter
        val itemTouch = ItemTouchHelper(SwipeCallback(adapter))
        itemTouch.attachToRecyclerView(recyclerIngredients)
    }

    /**
     * Adds onClickListener to add Ingredient Button and onKeyListener (ENTER) to EditText
     * from add Ingredient input
     */
    private fun setUpAddIngredient() {
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
     * Adds an ingredient to the list
     */
    private fun addIngredient() {
        val input: AutoCompleteTextView = findViewById(R.id.inputIngredient)
        val text = input.text.toString()
        val fakeIng = DBIngredient(0, text, 0, 0)
        if(text != "" && arrayListAdapter?.contains(fakeIng) == true && !ingredients.contains(
                fakeIng
            )) {
            val index = arrayListAdapter?.indexOf(fakeIng) as Int
            val ing = arrayListAdapter?.get(index) as DBIngredient
            ingredients.add(ing)
            arrayListAdapter?.remove(ing)
            arrayListAdapter?.notifyDataSetChanged()

            input.text.clear()
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            recyclerIngredients?.scrollToPosition(ingredients.size - 1)
            val focus = this.currentFocus
            if (focus != null) {
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focus.windowToken, 0)
            }
        }
    }

    /**
     * Creates new Recipe in Database
     */
    private fun submit() {
        val title = findViewById<EditText>(R.id.recipeTitle)
        val titleText = title.text.toString()
        val description = findViewById<EditText>(R.id.description)
        val descriptionText = description.text.toString()

        if(titleText != "" && descriptionText != "" && ingredients.size > 0) {
            val bitmap: Bitmap? = if(this.imageBitmap != null) {
                imageBitmap as Bitmap
            } else {
                null
            }
            val recipe = DBRecipe(0,titleText,descriptionText, bitmap)
            val id = db.addRecipe(recipe)
            if(id > -1) {
                for(ingredient in ingredients) {
                    db.addQuantity(DBQuantity(id.toInt(), ingredient.id, "1", ingredient.name))
                }
            }
            Toast.makeText(this,"Rezept hochgeladen!", Toast.LENGTH_SHORT).show()
            // Clear Input fields
            title.text.clear()
            description.text.clear()
            ingredients.clear()
            recyclerIngredients?.adapter?.notifyDataSetChanged()
            imgButton?.setImageBitmap(placeholder)
        } else {
            Toast.makeText(this,"Es muss mindestens der Titel, die Zubereitung und eine Zutat eingetragen sein", Toast.LENGTH_SHORT).show()
        }
    }
}