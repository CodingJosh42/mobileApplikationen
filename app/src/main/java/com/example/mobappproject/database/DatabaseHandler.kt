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
        createRecipeList(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVerison: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITY)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT)
        }
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

    fun getStoredIngredients(){

    }

    /**
     *
     */
    fun addStoreIngredient(ingredient: DBIngredient): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_STORED, 1)
        val writeDB = db.update(TABLE_INGREDIENT,contentValues, INGREDIENT_KEY_ID +"="+ ingredient.id,null)
        db.close()
        return writeDB
    }

    fun removeStoreIngredient(ingredient: DBIngredient): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_STORED, 0)
        val writeDB = db.update(TABLE_INGREDIENT,contentValues, INGREDIENT_KEY_ID +"="+ ingredient.id,null)
        db.close()
        return writeDB

    }

    /**
     * Creating Sample Ingredients into the Database
     *
     */
    private fun createIngredientList(db: SQLiteDatabase?){
        val ing = arrayListOf<String>("Apfel", "Käse","Knoblauch","Milch","Gurke","Tomate","Schinken", "Mehl", "Mozzarella", "Salat", "Zitrone", "Olivenöl", "Essig", "Kartoffel")
        val spice = arrayListOf<String>("Salz",  "Pfeffer")
        var name:String
        for(i in ing.indices){
            name = ing[i]
            db?.execSQL("INSERT INTO $TABLE_INGREDIENT ($INGREDIENT_KEY_NAME) " +
                    "VALUES(\"" +name +"\");")
        }
        for(i in spice.indices){
            name = spice[i]
            db?.execSQL("INSERT INTO $TABLE_INGREDIENT ($INGREDIENT_KEY_NAME,$INGREDIENT_KEY_SPICE) " +
                    "VALUES(\"" +name +"\",1);")
        }
    }

    /**
     * Creating Recipe samples into the Database
     */
    private fun createRecipeList(db: SQLiteDatabase?){
        val recipes = arrayListOf<DBRecipe>()
        recipes.add(DBRecipe(0,"Sommersalat", "Das Gemüse waschen und danach nach belieben klein schneiden. Mozzarella abtropfen lassen und alles in eine Schüssel geben. \n Je nach belieben Zitronen auspressen und mit Olivenöl und etwas Essig abschmecken. \n Schon ist der Salat fertig!", "", null))
        recipes.add(DBRecipe(0,"Pommes", "Die Kartoffeln waschen und danach in Streifen schneiden. Die Dicke der Streifen, können Sie nach Belieben selbst bestimmen. Danach alles mit Öl benetzen. Jetzt können die Kartoffeln in den Ofen, bis sie goldbraun sind. \n Danach nur noch salzen und fertig sind die Selbstgemachten Pommes!", "", null))
        val contentValues = ContentValues()
        val quantitys = arrayListOf<DBQuantity>()
        quantitys.add(DBQuantity(1,5, "3 große"))
        quantitys.add(DBQuantity(1,6,"1"))
        quantitys.add(DBQuantity(1,10,"nach Belieben"))
        quantitys.add(DBQuantity(1,11,"1/2"))
        quantitys.add(DBQuantity(1,12,""))
        quantitys.add(DBQuantity(1,13,""))
        quantitys.add(DBQuantity(2,14, "1kg"))
        quantitys.add(DBQuantity(2,15,"nach Belieben"))
        for (i in recipes.indices){
            contentValues.clear()
            db?.execSQL("INSERT INTO $TABLE_RECIPE ($RECIPE_KEY_NAME,$RECIPE_KEY_DESCRIPTION,$RECIPE_KEY_PICTURE) " +
                    "VALUES(\"" + recipes[i].name +"\", \" "+ recipes[i].description +"\", \" "+ recipes[i].picture + "\" );")
        }
        for (i in quantitys.indices){
            contentValues.clear()
            contentValues.put(QUANTITY_KEY_RECIPEID,quantitys[i].recipe_id)
            contentValues.put(QUANTITY_KEY_INGREDIENTID,quantitys[i].ingredient_id)
            contentValues.put(QUANTITY_KEY_QUANTITY,quantitys[i].quantity)
            db?.insert(TABLE_QUANTITY,null,contentValues)
        }
    }




}