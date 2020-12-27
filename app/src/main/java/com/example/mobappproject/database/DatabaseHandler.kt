package com.example.mobappproject.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mobappproject.dataClasses.Ingredient

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME,null, DB_Version){

    companion object{
        private const val DB_NAME = "Recipe_Database"
        private const val DB_Version = 1
        private const val TABLE_USER = "UserTable"
        private const val TABLE_INGREDIENT = "IngredientsTable"
        private const val TABLE_QUANTITY = "QuantityTable"
        private const val TABLE_RECIPE = "RecipeTable"

        //TABLE_USER Content
        private const val USER_KEY_ID = "_id"
        //TABLE_INGREDIENT Content
        private const val INGREDIENT_KEY_ID = "_id"
        private const val INGREDIENT_KEY_NAME = "name"
        private const val INGREDIENT_KEY_SPICE = "spice"
        private const val INGREDIENT_KEY_STORED = "STORED"
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
                + INGREDIENT_KEY_ID + "INTEGER PRIMARY KEY,"
                + INGREDIENT_KEY_NAME + "TEXT,"
                + INGREDIENT_KEY_SPICE + "INTEGER DEFAULT 0,"
                + INGREDIENT_KEY_STORED + "INTEGER DEFAULT 0" + ")")

        val CREATE_RECIPE_TABLE = ("CREATE TABLE " + TABLE_RECIPE + "("
                + RECIPE_KEY_ID + "INTEGER PRIMARY KEY,"
                + RECIPE_KEY_NAME + "TEXT,"
                + RECIPE_KEY_DESCRIPTION + "TEXT,"
                + RECIPE_KEY_PICTURE + "TEXT" + ")")

        val CREATE_QUANTITY_TABLE = ("CREATE TABLE " + TABLE_QUANTITY + "("
                + QUANTITY_KEY_INGREDIENTID + "INTEGER NOT NULL,"
                + QUANTITY_KEY_RECIPEID + "INTEGER NOT NULL,"
                + QUANTITY_KEY_QUANTITY + "TEXT" + ")")

        db?.execSQL(CREATE_INGREDIENT_TABLE)
        db?.execSQL(CREATE_RECIPE_TABLE)
        db?.execSQL(CREATE_QUANTITY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVerison: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITY)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT)
        onCreate(db)
    }

    fun addIngredient(ing: DBIngredient): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_NAME, ing.name)

        val writeDB = db.insert(TABLE_INGREDIENT, null,contentValues )
        db.close()
        return writeDB
    }

    fun addRecipe(recipe: DBRecipe):Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(RECIPE_KEY_NAME, recipe.name)
        contentValues.put(RECIPE_KEY_DESCRIPTION, recipe.description)
        contentValues.put(RECIPE_KEY_PICTURE, recipe.picture)
        val writeDB = db.insert(TABLE_RECIPE, null, contentValues)



        // Connect Recipe and Ingredients
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



}