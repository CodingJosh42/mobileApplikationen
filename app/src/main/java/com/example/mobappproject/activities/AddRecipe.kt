package com.example.mobappproject.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore

import android.view.Menu
import android.view.MenuItem

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DBQuantity
import com.example.mobappproject.database.DBRecipe
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.arrayListAdapter.ArrayListAdapter
import com.example.mobappproject.recylcerQuantitys.RecyclerAdapterQuantity
import com.example.mobappproject.recylcerQuantitys.SwipeCallbackQuantity
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import kotlin.math.ceil

/**
 * Add Recipe Activity
 */
class AddRecipe : AppCompatActivity() {

    private var quantitys = ArrayList<DBQuantity>()
    private var availableIngredients = ArrayList<DBIngredient>()
    private var db = DatabaseHandler(this)
    private var recyclerQuantitys: RecyclerView? = null
    private var arrayListAdapter: ArrayListAdapter? = null
    private val ADD_RECIPE_IMAGE = 1
    private var imageBitmap: Bitmap? = null
    private var placeholder: Bitmap? = null

    private var imgButton: ImageButton? = null
    private var inputIngredient: AutoCompleteTextView? = null
    private var inputQuantity: EditText? = null
    private var title: EditText? = null
    private var description: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)
        imgButton = findViewById(R.id.imageInput)
        inputIngredient = findViewById(R.id.inputIngredient)
        inputQuantity = findViewById(R.id.quantity)
        title = findViewById(R.id.recipeTitle)
        description = findViewById(R.id.description)

        loadIngredients()

        setUpAddIngredient()

        setRecyclerView()

        setUpImageInput()
    }

    /**
     * Creates OptionsMenu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_recipe, menu)
        return true
    }

    /**
     * Handles selected menu Item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.uploadRecipe -> {
                submit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Loads all ingredients from the database
     */
    private fun loadIngredients() {
        val dbIngs = db.getIngredients()
        availableIngredients.addAll(dbIngs)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_RECIPE_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data as Uri;
            imageBitmap = getImage(selectedImage)
            imgButton?.setImageBitmap(imageBitmap)
        }
    }

    /**
     * Loads image as Bitmap. Compresses Image so its not too big for the Database
     * @param uri Path of file
     * @return Returns Bitmap of image
     */
    private fun getImage(uri: Uri): Bitmap {
        // Get Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        val byteArray = stream.toByteArray()

        // Check size
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
        val height = 500
        val width = 500
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
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    }

    /**
     * Sets up the RecylcerView of ingredients. setUpAddIngredient must be called before this
     */
    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerQuantitys = findViewById(R.id.ingredients)
        recyclerQuantitys?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapterQuantity(quantitys, arrayListAdapter!!)
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
        title?.setOnEditorActionListener(TextView.OnEditorActionListener { view, keyCode, event ->
            return@OnEditorActionListener closeKeyboardOnEnter(keyCode, event)
        })
        inputIngredient?.setOnEditorActionListener(TextView.OnEditorActionListener { view, keyCode, event ->
            return@OnEditorActionListener closeKeyboardOnEnter(keyCode, event)
        })
        inputQuantity?.setOnEditorActionListener(TextView.OnEditorActionListener { view, keyCode, event ->
            return@OnEditorActionListener closeKeyboardOnEnter(keyCode, event)
        })
        this.arrayListAdapter = ArrayListAdapter(this,
                R.layout.simple_dropdown_item_1line, availableIngredients)
        inputIngredient?.threshold = 1
        inputIngredient?.setAdapter(arrayListAdapter)
    }

    /**
     * Closes Keyboard on Enter
     * @param keyCode key code (EditorInfo)
     * @param event event
     * @return Returns true if user pressed Enter, else false
     */
    private fun closeKeyboardOnEnter(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            return true
        } else if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
            hideKeyboard()
            return true
        }
        return false
    }

    /**
     * Hides Keyboard
     */
    private fun hideKeyboard() {
        val focus = this.currentFocus
        if (focus != null) {
            val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focus.windowToken, 0)
        }
    }

    /**
     * Adds an ingredient to the list
     */
    private fun addIngredient() {
        val text = inputIngredient?.text.toString()
        val fakeIng = DBIngredient(0, text, 0, 0)
        if (text != "" && arrayListAdapter?.contains(fakeIng) == true) {
            val index = arrayListAdapter?.indexOf(fakeIng) as Int
            val ing = arrayListAdapter?.get(index) as DBIngredient
            val quantityText = inputQuantity?.text.toString()
            val quantity = DBQuantity(0, ing.id, quantityText, ing.name)
            quantity.ingredient = ing
            quantitys.add(quantity)

            arrayListAdapter?.remove(ing)
            arrayListAdapter?.notifyDataSetChanged()

            inputIngredient?.text?.clear()
            inputQuantity?.text?.clear()

            recyclerQuantitys?.adapter?.notifyDataSetChanged()
            recyclerQuantitys?.scrollToPosition(quantitys.size - 1)

            hideKeyboard()
        } else if (text != "") {
            Toast.makeText(this, "$text ist keine valide Zutat oder bereits in deiner Liste", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Creates new Recipe in Database
     */
    private fun submit() {
        val titleText = title?.text.toString()
        val descriptionText = description?.text.toString()

        if (titleText != "" && descriptionText != "" && quantitys.size > 0) {
            val bitmap: Bitmap? = if (this.imageBitmap != null) {
                imageBitmap as Bitmap
            } else {
                null
            }
            val recipe = DBRecipe(0, titleText, descriptionText, bitmap)
            val id = db.addRecipe(recipe)
            if (id > -1) {
                for (quantity in quantitys) {
                    quantity.recipe_id = id.toInt()
                    db.addQuantity(quantity)
                }
                val layout = findViewById<ConstraintLayout>(R.id.constLayout)
                Snackbar.make(layout, "Rezept hochgeladen", Snackbar.LENGTH_LONG)
                        .setAction("Rezept anzeigen") {
                            val intent = Intent(this, ShowRecipe::class.java)
                            intent.putExtra("Id", id.toInt())
                            startActivity(intent)
                        }.show()

                clearInput()
            } else {
                Toast.makeText(this, "Rezept konnte nicht hochgeladen werden", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Es muss mindestens der Titel, die Zubereitung und eine Zutat eingetragen sein", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Clears Input fields
     */
    private fun clearInput() {
        title?.text?.clear()
        description?.text?.clear()
        inputQuantity?.text?.clear()
        inputIngredient?.text?.clear()
        quantitys.clear()
        recyclerQuantitys?.adapter?.notifyDataSetChanged()
        imgButton?.setImageBitmap(placeholder)
        imageBitmap = placeholder
    }
}