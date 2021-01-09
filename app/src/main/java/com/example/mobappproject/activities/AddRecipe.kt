package com.example.mobappproject.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.mobappproject.recylcerQuantitys.RecyclerAdapterQuantity
import com.example.mobappproject.recylcerQuantitys.SwipeCallbackQuantity
import java.io.ByteArrayOutputStream
import kotlin.math.ceil


class AddRecipe : AppCompatActivity() {

    private var quantitys = ArrayList<DBQuantity>()
    private var availableIngredients = ArrayList<DBIngredient>()
    private var db = DatabaseHandler(this)
    private var recyclerQuantitys: RecyclerView ?= null
    private var arrayListAdapter: ArrayListAdapter ?= null
    private val ADD_RECIPE_IMAGE = 1
    private var imageBitmap: Bitmap ?= null
    private var placeholder: Bitmap ?= null

    private var imgButton: ImageButton ?= null
    private var inputIngredient: AutoCompleteTextView ?= null
    private var inputQuantity: EditText ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)
        imgButton = findViewById(R.id.imageInput)
        inputIngredient = findViewById(R.id.inputIngredient)
        inputQuantity = findViewById(R.id.quantity)

        loadIngredients()

        setUpAddIngredient()

        setRecyclerView()

        setUpImageInput()

        setUpSubmit()
    }

    /**
     * Loads all ingredients from the database
     */
    private fun loadIngredients() {
        val dbIngs = db.getIngredients()
        availableIngredients.addAll(dbIngs)
    }

    /**
     * Sets up submit Button
     */
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
            pickPicture()
        }
    }

    /**
     * Starts new activity. User can pick an image and returns to this activity
     */
    private fun pickPicture() {
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
            imageBitmap = getImage(selectedImage)
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
        } else {
            if (heightRatio > widthRatio) {
                options.inSampleSize = ceil(((height.toFloat() / options.outHeight).toDouble())).toInt()
            } else {
                options.inSampleSize = ceil((width.toFloat() / options.outWidth).toDouble()).toInt()
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
        recyclerQuantitys = findViewById(R.id.ingredients)
        recyclerQuantitys?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapterQuantity(quantitys, arrayListAdapter!!, null)
        recyclerQuantitys?.adapter = adapter
        val itemTouch = ItemTouchHelper(SwipeCallbackQuantity(adapter))
        itemTouch.attachToRecyclerView(recyclerQuantitys)
    }

    /**
     * Adds onClickListener to add Ingredient Button and onKeyListener (ENTER) to EditText
     * from add Ingredient input
     */
    private fun setUpAddIngredient() {
        val button = findViewById<Button>(R.id.addIngredient)
        button.setOnClickListener {
            addIngredient()
        }
        val inputIng: AutoCompleteTextView = findViewById(R.id.inputIngredient)

        this.arrayListAdapter = ArrayListAdapter(this,
            R.layout.simple_dropdown_item_1line, availableIngredients)
        inputIng.threshold = 1
        inputIng.setAdapter(arrayListAdapter)
    }

    /**
     * Adds an ingredient to the list
     */
    private fun addIngredient() {
        val text = inputIngredient?.text.toString()
        val fakeIng = DBIngredient(0, text, 0, 0)
        if(text != "" && arrayListAdapter?.contains(fakeIng) == true) {
            val index = arrayListAdapter?.indexOf(fakeIng) as Int
            val ing = arrayListAdapter?.get(index) as DBIngredient
            val quantityText = inputQuantity?.text.toString()
            val quantity = DBQuantity(0,ing.id,quantityText,ing.name)
            quantity.ingredient = ing
            quantitys.add(quantity)

            arrayListAdapter?.remove(ing)
            arrayListAdapter?.notifyDataSetChanged()

            inputIngredient?.text?.clear()
            inputQuantity?.text?.clear()

            recyclerQuantitys?.adapter?.notifyDataSetChanged()
            recyclerQuantitys?.scrollToPosition(quantitys.size - 1)

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

        if(titleText != "" && descriptionText != "" && quantitys.size > 0) {
            val bitmap: Bitmap? = if(this.imageBitmap != null) {
                imageBitmap as Bitmap
            } else {
                null
            }
            val recipe = DBRecipe(0,titleText,descriptionText, bitmap)
            val id = db.addRecipe(recipe)
            if(id > -1) {
                for(quantity in quantitys) {
                    quantity.recipe_id = id.toInt()
                    db.addQuantity(quantity)
                }
                Toast.makeText(this,"Rezept hochgeladen!", Toast.LENGTH_SHORT).show()

                // Clear Input fields
                title.text.clear()
                description.text.clear()
                inputQuantity?.text?.clear()
                inputIngredient?.text?.clear()
                quantitys.clear()
                recyclerQuantitys?.adapter?.notifyDataSetChanged()
                imgButton?.setImageBitmap(placeholder)
            } else {
                Toast.makeText(this,"Rezept konnte nicht hochgeladen werden", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this,"Es muss mindestens der Titel, die Zubereitung und eine Zutat eingetragen sein", Toast.LENGTH_LONG).show()
        }
    }
}