package com.example.mobappproject.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.mobappproject.dataClasses.Ingredient

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME,null, DB_Version){

    companion object{
        private const val DB_NAME = "Recipe_Database"
        private const val DB_Version = 1
        private const val TABLE_INGREDIENT = "IngredientTable"
        private const val TABLE_QUANTITY = "QuantityTable"
        private const val TABLE_RECIPE = "RecipeTable"

        //TABLE_INGREDIENT Content
        private const val INGREDIENT_KEY_ID = "_id"
        private const val INGREDIENT_KEY_NAME = "name"
        private const val INGREDIENT_KEY_SPICE = "spice"
        private const val INGREDIENT_KEY_STORED = "stored"
        //TABLE_QUANTITY Content
        private const val QUANTITY_KEY_INGREDIENTID = "ingredient_id"
        private const val QUANTITY_KEY_RECIPEID = "recipe_id"
        private const val QUANTITY_KEY_QUANTITY = "quantity"
        //TABLE_RECIPE Content
        private const val RECIPE_KEY_ID = "_id"
        private const val RECIPE_KEY_NAME = "name"
        private const val RECIPE_KEY_DESCRIPTION = "description"
        private const val RECIPE_KEY_PICTURE = "picture"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_INGREDIENT_TABLE = ("CREATE TABLE " + TABLE_INGREDIENT + "("
                + INGREDIENT_KEY_ID + " INTEGER PRIMARY KEY,"
                + INGREDIENT_KEY_NAME + " TEXT,"
                + INGREDIENT_KEY_SPICE + " INTEGER DEFAULT 0,"
                + INGREDIENT_KEY_STORED + " INTEGER DEFAULT 0" + ")")

        val CREATE_RECIPE_TABLE = ("CREATE TABLE " + TABLE_RECIPE + "("
                + RECIPE_KEY_ID + " INTEGER PRIMARY KEY,"
                + RECIPE_KEY_NAME + " TEXT,"
                + RECIPE_KEY_DESCRIPTION + " TEXT,"
                + RECIPE_KEY_PICTURE + " TEXT" + ")")

        val CREATE_QUANTITY_TABLE = ("CREATE TABLE " + TABLE_QUANTITY + "("
                + QUANTITY_KEY_INGREDIENTID + " INTEGER NOT NULL,"
                + QUANTITY_KEY_RECIPEID + " INTEGER NOT NULL,"
                + QUANTITY_KEY_QUANTITY + " TEXT" + ")")

        db?.execSQL(CREATE_INGREDIENT_TABLE)
        db?.execSQL(CREATE_RECIPE_TABLE)
        db?.execSQL(CREATE_QUANTITY_TABLE)
        createIngredientList(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVerison: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITY)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT)
        onCreate(db)
    }

    fun addIngredient(name: String): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_NAME, name)

        val writeDB = db.insert(TABLE_INGREDIENT, null,contentValues )
        db.close()
        return writeDB
    }

    fun addSpice(name: String): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_NAME, name)
        contentValues.put(INGREDIENT_KEY_SPICE, 1)

        val writeDB = db.insert(TABLE_INGREDIENT, null,contentValues )
        db.close()
        return writeDB
    }

    fun addRecipe(recipe: DBRecipe):Long{
        val db = this.writableDatabase

        //GET ID FROM THIS RECIPE

        val contentValues = ContentValues()
        contentValues.put(RECIPE_KEY_NAME, recipe.name)
        contentValues.put(RECIPE_KEY_DESCRIPTION, recipe.description)
        contentValues.put(RECIPE_KEY_PICTURE, recipe.picture)
        val writeDB = db.insert(TABLE_RECIPE, null, contentValues)
        // Connect Recipe and Ingredients needs to be done
        db.close()
        return writeDB

    }

    fun addQuantity(quantity: DBQuantity):Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(QUANTITY_KEY_INGREDIENTID, quantity.ingredient_id)
        contentValues.put(QUANTITY_KEY_RECIPEID, quantity.recipe_id)
        contentValues.put(QUANTITY_KEY_QUANTITY, quantity.quantity)
        val writeDB = db.insert(TABLE_QUANTITY, null, contentValues)
        db.close()

        return writeDB
    }

    /**
     * Adds all Ingredients into the Database
     *
     */
    fun createIngredientList(db: SQLiteDatabase?){
        val ing = arrayListOf<String>("Apfel", "Käse","Knoblauch","Milch","Gurke","Tomate","Schinken", "Mehl")
        val spice = arrayListOf<String>("Salz",  "Pfeffer")
        var name:String
        for(i in ing.indices){
            name = ing[i]
            db?.execSQL("INSERT INTO $TABLE_INGREDIENT ($INGREDIENT_KEY_NAME) " +
                    "VALUES(\"" +name +"\");")
        }
        for(i in spice.indices){
            name = ing[i]
            db?.execSQL("INSERT INTO $TABLE_INGREDIENT ($INGREDIENT_KEY_NAME,$INGREDIENT_KEY_SPICE) " +
                    "VALUES(\"" +name +"\",1);")
        }
    }

    /**
     * Get all Ingredients from Database
     * return List with all Ingredients
     */
    fun getIngredients():ArrayList<DBIngredient>{
        val list: ArrayList<DBIngredient> = ArrayList()
        val select = "SELECT * FROM $TABLE_INGREDIENT"
        val db = this.readableDatabase
        val cursor: Cursor?
        //Values for each Element of the Table
        var id: Int
        var name: String
        var stored: Int
        var spice: Int

        try{
            cursor = db.rawQuery(select,null)
        }catch (e: SQLiteException){
            db.execSQL(select)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex(INGREDIENT_KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(INGREDIENT_KEY_NAME))
                stored = cursor.getInt(cursor.getColumnIndex(INGREDIENT_KEY_STORED))
                spice = cursor.getInt(cursor.getColumnIndex(INGREDIENT_KEY_SPICE))
                val newIngredient = DBIngredient(id = id, name = name, stored = stored, spice = spice)
                list.add(newIngredient)
            }while (cursor.moveToNext())
        }
        return list
    }

    fun addStoreIngredient(){

    }

    fun removeStoreIngredient(){

    }



}