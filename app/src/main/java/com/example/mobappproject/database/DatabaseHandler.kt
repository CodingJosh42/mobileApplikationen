package com.example.mobappproject.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mobappproject.dataClasses.Ingredient

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME,null, DB_Version){

    companion object{
        private const val DB_NAME = "Recipe_Database"
        private const val DB_Version = 1
        private const val TABLE_USER = "UserTable"
        private const val TABLE_INGREDIENTS = "IngredientsTable"
        private const val TABLE_QUANTITY = "QuantityTable"
        private const val TABLE_RECIPE = "RecipeTable"

        //TABLE_USER Content
        private const val USER_KEY_ID = "_id"
        //TABLE_INGREDIENTS Content
        private const val INGREDIENT_KEY_ID = "_id"
        private const val INGREDIENT_KEY_NAME = "name"
        //TABLE_QUANTITY Content
        private const val QUANTITY_KEY_INGREDIENTID = "ingredient_id"
        private const val QUANTITY_KEY_RECIPEID = "recipe_id"
        //TABLE_RECIPE Content


    }

    override fun onCreate(p0: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }


}