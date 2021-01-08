package com.example.mobappproject.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import androidx.core.database.getBlobOrNull
import com.example.mobappproject.R
import java.io.ByteArrayOutputStream


class DatabaseHandler(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_Version){

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
        private const val RECIPE_KEY_NAME = "title"
        private const val RECIPE_KEY_DESCRIPTION = "description"
        private const val RECIPE_KEY_PICTURE = "picture"
    }

    /**
     * Creates the database
     */
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
                + RECIPE_KEY_PICTURE + " BLOB" + ")")

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

    /**
     * Drops the actual Database and creates a new one
     * @param db oldDatabase
     * @param oldVerison old version if the database
     * @param newVersion new version of the database
     */
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

        val writeDB = db.insert(TABLE_INGREDIENT, null, contentValues)
        db.close()
        return writeDB
    }



    fun addSpice(name: String): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_NAME, name)
        contentValues.put(INGREDIENT_KEY_SPICE, 1)

        val writeDB = db.insert(TABLE_INGREDIENT, null, contentValues)
        db.close()
        return writeDB
    }

    fun addRecipe(recipe: DBRecipe):Long{
        val db = this.writableDatabase

        //GET ID FROM THIS RECIPE

        val contentValues = ContentValues()
        contentValues.put(RECIPE_KEY_NAME, recipe.name)
        contentValues.put(RECIPE_KEY_DESCRIPTION, recipe.description)
        if(recipe.picture != null) {
            val byteArray = getByteArray(recipe.picture)
            contentValues.put(RECIPE_KEY_PICTURE, byteArray)
        }
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
     * @return List with all Ingredients
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
            cursor = db.rawQuery(select, null)
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

    /**
     * Get filtered Ingredients from Database
     * @param mode select the searchmode
     * @return List with chosen Ingredients
     */
    fun getIngredients(mode: Int):ArrayList<DBIngredient>{
        val list: ArrayList<DBIngredient> = ArrayList()
        val select:String
        when (mode) {
            0 -> select = "SELECT * FROM $TABLE_INGREDIENT WHERE $INGREDIENT_KEY_SPICE = 0"
            1 -> select = "SELECT * FROM $TABLE_INGREDIENT WHERE $INGREDIENT_KEY_SPICE = 1"
            2 -> select = "SELECT * FROM $TABLE_INGREDIENT WHERE $INGREDIENT_KEY_SPICE = 0 AND $INGREDIENT_KEY_STORED = 1"
            3 -> select = "SELECT * FROM $TABLE_INGREDIENT WHERE $INGREDIENT_KEY_SPICE = 1 AND $INGREDIENT_KEY_STORED = 1"
            else-> select = "SELECT * FROM $TABLE_INGREDIENT WHERE $INGREDIENT_KEY_SPICE = 2"
        }

        val db = this.readableDatabase
        val cursor: Cursor?
        //Values for each Element of the Table
        var id: Int
        var name: String
        var stored: Int
        var spice: Int

        try{
            cursor = db.rawQuery(select, null)
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


    /**
     *  Stores a selected Ingredient
     *  @param ingredient the Ingredient, that will be stored
     *  @return status of the update
     */
    fun addStoreIngredient(ingredient: DBIngredient): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_STORED, 1)
        val writeDB = db.update(TABLE_INGREDIENT, contentValues, INGREDIENT_KEY_ID + "=" + ingredient.id, null)
        db.close()
        return writeDB
    }

    /**
     * Removes a selected Ingredient from storage
     * @param ingredient the Ingredient, that will be removed
     * @return status of the update
     */
    fun removeStoreIngredient(ingredient: DBIngredient): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INGREDIENT_KEY_STORED, 0)
        val writeDB = db.update(TABLE_INGREDIENT, contentValues, INGREDIENT_KEY_ID + "=" + ingredient.id, null)
        db.close()
        return writeDB

    }

    /**
     * Get all quantitys for a specific recipe
     * @param recipeID ID of the recipe
     * @return Arraylist of quantitys
     */
    fun getRecipeQuantitys(recipeID: Int): ArrayList<DBQuantity>{
        val list: ArrayList<DBQuantity> = ArrayList()
        val select = "SELECT * FROM $TABLE_INGREDIENT JOIN $TABLE_QUANTITY" +
                " ON $TABLE_INGREDIENT.$INGREDIENT_KEY_ID = $TABLE_QUANTITY.$QUANTITY_KEY_INGREDIENTID" +
                " JOIN $TABLE_RECIPE" +
                " ON $TABLE_QUANTITY.$QUANTITY_KEY_RECIPEID = $TABLE_RECIPE.$RECIPE_KEY_ID " +
                " WHERE $TABLE_RECIPE.$RECIPE_KEY_ID = $recipeID"
        val db = this.readableDatabase
        val cursor: Cursor?
        //Values for each Element of the Table
        var recipe_id: Int
        var ingredient_id :Int
        var quantity: String
        var ingredient_name: String

        try{
            cursor = db.rawQuery(select, null)
        }catch (e: SQLiteException){
            db.execSQL(select)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do {
                recipe_id = cursor.getInt(cursor.getColumnIndex(QUANTITY_KEY_RECIPEID))
                ingredient_id = cursor.getInt(cursor.getColumnIndex(QUANTITY_KEY_INGREDIENTID))
                quantity = cursor.getString(cursor.getColumnIndex(QUANTITY_KEY_QUANTITY))
                ingredient_name = cursor.getString(cursor.getColumnIndex("$INGREDIENT_KEY_NAME"))
                val newQuantity = DBQuantity(recipe_id = recipe_id, ingredient_id = ingredient_id, quantity = quantity, ingredientName = ingredient_name)
                list.add(newQuantity)
            }while (cursor.moveToNext())
        }
        return list
    }

    fun getRecipeByID(recipeID: Int): DBRecipe?{
        val db = this.readableDatabase
        val select = "SELECT * FROM $TABLE_RECIPE WHERE $RECIPE_KEY_ID = $recipeID"
        val cursor: Cursor?
        var recipe: DBRecipe
        //Values for the Recipe
        var id: Int
        var name :String
        var description: String
        var picture: ByteArray?
        var pictureBitmap: Bitmap?

        try{
            cursor = db.rawQuery(select, null)
        }catch (e: SQLiteException){
            db.execSQL(select)
            return null
        }
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndex(RECIPE_KEY_ID))
            name = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_NAME))
            description = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_DESCRIPTION))
            picture = cursor.getBlobOrNull(cursor.getColumnIndex(RECIPE_KEY_PICTURE))
            pictureBitmap = if(picture != null) {
                getBitmap(picture)
            } else {
                null
            }
            val newRecipe = DBRecipe(id = id, name = name, description = description, picture = pictureBitmap)
            return newRecipe
        }
        return null
    }

    /**
     * Get all Recipes
     * @return Arraylist of recipes
     */
    fun getRecipes(): ArrayList<DBRecipe>{
        val list: ArrayList<DBRecipe> = ArrayList()
        val select = "SELECT * FROM $TABLE_RECIPE"
        val db = this.readableDatabase
        val cursor: Cursor?
        //Values for each Element of the Table
        var id: Int
        var name :String
        var description: String
        var picture: ByteArray?
        var pictureBitmap: Bitmap?

        try{
            cursor = db.rawQuery(select, null)
        }catch (e: SQLiteException){
            db.execSQL(select)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex(RECIPE_KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_NAME))
                description = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_DESCRIPTION))
                picture = cursor.getBlobOrNull(cursor.getColumnIndex(RECIPE_KEY_PICTURE))
                pictureBitmap = if(picture != null) {
                    getBitmap(picture)
                } else {
                    null
                }
                val newRecipe = DBRecipe(id = id, name = name, description = description, picture = pictureBitmap)
                list.add(newRecipe)
            }while (cursor.moveToNext())
        }
        return list
    }

    /**
     * Converts ByteArray to Bitmap
     */
    private fun getBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size);
    }

    /**
     * Loads image as Bitmap
     * @param Ressource Id
     */
    private fun getImage(resId: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, resId)
    }

    /**
     * Converts a Bitmap to a ByteArray
     */
    private fun getByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    fun searchRecipes(storage: ArrayList<DBIngredient>, search: String): ArrayList<DBRecipe>{
        var recipeList: ArrayList<DBRecipe> = ArrayList()
        val db = this.readableDatabase
        val cursor: Cursor?
        //Values for each Element of the Table
        var id: Int
        var title :String
        var description: String
        var picture: ByteArray? = null
        var pictureBitmap: Bitmap?
        var matches: Int
        //Build SELECT String
        var select: String = "SELECT *, COUNT (*) as Matches FROM $TABLE_INGREDIENT JOIN $TABLE_QUANTITY" +
                " ON $TABLE_INGREDIENT.$INGREDIENT_KEY_ID = $TABLE_QUANTITY.$QUANTITY_KEY_INGREDIENTID" +
                " JOIN $TABLE_RECIPE" +
                " ON $TABLE_QUANTITY.$QUANTITY_KEY_RECIPEID = $TABLE_RECIPE.$RECIPE_KEY_ID "
        if (storage.isNotEmpty()){
            select += "WHERE ("
            for (i in storage.indices){
                if (i == 0){
                    select += "$TABLE_INGREDIENT.$INGREDIENT_KEY_ID = " + storage[i].id
                }else{
                    select += " OR $TABLE_INGREDIENT.$INGREDIENT_KEY_ID = " + storage[i].id
                }
            }
            select += ") AND $RECIPE_KEY_NAME LIKE \"%$search%\""
        }else {
            select += "WHERE $RECIPE_KEY_NAME LIKE \"%$search%\""
        }
        select += " GROUP BY $TABLE_RECIPE.$RECIPE_KEY_ID" +
                " ORDER BY Matches DESC"
        try{
            cursor = db.rawQuery(select, null)
        }catch (e: SQLiteException){
            db.execSQL(select)
            return ArrayList()
        }
        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex(RECIPE_KEY_ID))
                title = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_NAME))
                description = cursor.getString(cursor.getColumnIndex(RECIPE_KEY_DESCRIPTION))
                picture = cursor.getBlobOrNull(cursor.getColumnIndex(RECIPE_KEY_PICTURE))
                pictureBitmap = if(picture != null) {
                    getBitmap(picture)
                } else {
                    null
                }
                matches = cursor.getInt(cursor.getColumnIndex("Matches"))
                val newRecipe = DBRecipe(id = id, name = title, description = description, picture = pictureBitmap)
                newRecipe.matches = matches
                recipeList.add(newRecipe)
            }while (cursor.moveToNext())
        }
        return recipeList

    }

    /**
     * Creating Sample Ingredients into the Database
     * @param db CreatedDatabase
     */
    private fun createIngredientList(db: SQLiteDatabase?){
        val ing = arrayListOf<String>("Apfel", "Käse", "Knoblauch", "Milch", "Gurke", "Tomate", "Schinken", "Mehl", "Mozzarella", "Salat", "Zitrone", "Olivenöl", "Essig", "Kartoffel")
        val spice = arrayListOf<String>("Salz", "Pfeffer")
        var name:String
        for(i in ing.indices){
            name = ing[i]
            db?.execSQL("INSERT INTO $TABLE_INGREDIENT ($INGREDIENT_KEY_NAME) " +
                    "VALUES(\"" + name + "\");")
        }
        for(i in spice.indices){
            name = spice[i]
            db?.execSQL("INSERT INTO $TABLE_INGREDIENT ($INGREDIENT_KEY_NAME,$INGREDIENT_KEY_SPICE) " +
                    "VALUES(\"" + name + "\",1);")
        }
    }

    /**
     * Creating Recipe samples into the Database
     * @param db CreatedDatabase
     */
    private fun createRecipeList(db: SQLiteDatabase?){
        val placeholder = getImage(R.drawable.placeholder)
        val recipes = arrayListOf<DBRecipe>()
        recipes.add(DBRecipe(0, "Sommersalat", "Das Gemüse waschen und danach nach belieben klein schneiden. Mozzarella abtropfen lassen und alles in eine Schüssel geben. \n Je nach belieben Zitronen auspressen und mit Olivenöl und etwas Essig abschmecken. \n Schon ist der Salat fertig!", placeholder))
        recipes.add(DBRecipe(0, "Pommes", "Die Kartoffeln waschen und danach in Streifen schneiden. Die Dicke der Streifen, können Sie nach Belieben selbst bestimmen. Danach alles mit Öl benetzen. Jetzt können die Kartoffeln in den Ofen, bis sie goldbraun sind. \n Danach nur noch salzen und fertig sind die Selbstgemachten Pommes!", placeholder))
        val contentValues = ContentValues()
        val quantitys = arrayListOf<DBQuantity>()
        quantitys.add(DBQuantity(1, 5, "3 ", ""))
        quantitys.add(DBQuantity(1, 6, "1", ""))
        quantitys.add(DBQuantity(1, 10, "", ""))
        quantitys.add(DBQuantity(1, 11, "1/2", ""))
        quantitys.add(DBQuantity(1, 12, "", ""))
        quantitys.add(DBQuantity(1, 13, "", ""))
        quantitys.add(DBQuantity(2, 14, "1kg", ""))
        quantitys.add(DBQuantity(2, 15, "", ""))
        for (i in recipes.indices){
            contentValues.clear()
            contentValues.put(RECIPE_KEY_NAME, recipes[i].name)
            contentValues.put(RECIPE_KEY_DESCRIPTION, recipes[i].description)
            if(recipes[i].picture != null) {
                val byteArray = getByteArray(recipes[i].picture!!)
                contentValues.put(RECIPE_KEY_PICTURE, byteArray)
            }
            db?.insert(TABLE_RECIPE, null, contentValues)

        }
        for (i in quantitys.indices){
            contentValues.clear()
            contentValues.put(QUANTITY_KEY_RECIPEID, quantitys[i].recipe_id)
            contentValues.put(QUANTITY_KEY_INGREDIENTID, quantitys[i].ingredient_id)
            contentValues.put(QUANTITY_KEY_QUANTITY, quantitys[i].quantity)
            db?.insert(TABLE_QUANTITY, null, contentValues)
        }
    }




}