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
import kotlin.math.ceil


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
     * Loads image as Bitmap. Compresses Image so its not too big for the Database
     * @param Ressource Id
     */
    private fun getImage(resId: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, resId, options)
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
        return BitmapFactory.decodeResource(context.resources, resId, options)
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

        val ing = arrayListOf<String>("Açaí" ,"Ananas", "Apfel", "Aprikose", "Avocado", "Banane", "Birne", "Blaubeere", "Brombeere", "Cashew", "Clementine", "Cranberry", "Dattel", "Erdbeere", "Feige", "Goji-Beere", "Granatapfel", "Grapefruit", "Guave", "Hagebutte", "Himbeere", "Holunder", "Honigmelone", "Jackfrucht", "Kaki","Kaktusfeige", "Physalis", "Kirsche", "Kiwi", "Limette", "Litschi", "Mandarine", "Mango", "Orange", "Pampelmuse", "Papaya", "Passionsfrucht", "Pfirsich", "Pflaume", "Drachenfrucht", "Pomelo", "Preiselbeere","Sternfrucht", "Wassermelone", "Stachelbeere", "Weintraube", "Zitrone", "Zwetschge", "Artischocke", "Aubergine", "Bärlauch", "Blumenkohl", "Broccoli", "Erbsen", "Fenchel", "Grüne Bohne", "Gurke", "Kartoffel", "Knoblauch", "Kürbis", "Lauch", "Lauchzwiebel", "Paprika", "Radieschen", "Rhabarber", "Rosenkohl", "Rote Bete", "Rotkohl", "Schalotten", "Schnittlauch", "Spargel", "Spitzkohl", "Spinat", "Staudensellerie", "Süßkartoffel", "Tomate", "Weißkohl", "Wirsing", "Wilder Spargel", "Zuckermais", "Zucchini", "Zwiebel", "Zuckerschoten", "Reis", "Risotto-Reis", "Weißer Reis", "Basmati-Reis", "Wild-Reis", "Sushi-Reis", "Bomba-Reis", "Spaghetti-Nudeln", "Penne-Nudeln", "Makkaroni-Nudeln", "Makkaroni-Nudeln", "Bandnudeln", "Kuhmilch", "Honig", "Rapsöl", "Olivenöl", "Sonnenblumenöl", "Käse", "Mozzarella", "Rinderfilet", "Hackfleisch-Schwein", "Hackfleisch-Rind", "Hackfleisch-Lamm", "Hähnchenbrust", "Putenbrust", "Kochschinken", "Ei", "Joghurt", "Ketchup", "Barbecue Sauce", "Senf-süß", "Senf-mittelscharf", "Senf-scharf", "Mayonnaise", "Sauce Hollandaise", "Schmand", "Kefir", "Lachs", "Forelle", "Wildlachs", "Aal", "Seelachs", "Dorade", "Hering", "Thunfisch", "Karpfen", "Makrele", "Grieß", "Bulgur", "Kuskus", "Sahne", "Cherrytomate", "Tomatenmark", "Mehl", "Butter", )

        val spice = arrayListOf<String>("Ajowan", "Anis", "Annatto", "Asant", "Bärlauch", "Bärwurz", "Basilikum ", "Beifuß", "Berbere", "Bergkümmel", "Bertram", "Bockshornklee", "Bohnenkraut", "Borretsch", "Brotklee", "Brunnenkresse", "Cardamom", "Cayennepfeffer", "Chili", "Cilantro", "Cumin", "Curryblätter", "Currykraut", "Currypulver", "Dill", "Eberraute", "Engelwurz","Epazote", "Sumach", "Estragon", "Fenchel", "Fetthenne", "Gado-Gado", "Gänseblümchen", "Garam Masala", "Gewürznelke", "Gochujang", "Gomashio", "Harissa", "Herbes Fines", "Huflattich", "Ingwer", "Kaffernlimette", "Kakaopulver", "Kalmus", "Kapern", "Kapuzinerkresse", "Grüner Kardamom", "Schwarzer Kardamom", "Kerbel", "Kemirinuss", "Knoblauch", "Koriander", "Meerrettich", "Kresse", "Kreuzkümmel", "Kubebenpfeffer", "Kümmel", "Kurkuma", "Lakritze", "Lavendel", "Liebstöckel", "Lorbeer", "Löffelkraut", "Majoran", "Meerrettich", "Zitronenmelisse", "Minze", "Mitsuba", "Mohnsamen", "Muskat", "Piment", "Oregano", "Pandanus", "Paradieskörner", "Paprika", "Pastinake", "Petersilie", "Perilla", "Pfeffer weiß", "Pfeffer schwarz", "Pfeffer grün", "Pfefferminze", "Pimpinelle", "Quendel", "Ras el-Hanout", "Portulak", "Rosmarin", "Rouille", "Safran", "Salbei", "Salz", "Sambal", "Sassafras", "Sauerampfer", "Schabzigerklee", "Schafgarbe", "Schnittlauch", "Schwarzkümmel", "Senf", "Soumbala", "Spitzwegerich", "Sternanis", "Stevia", "Sumach", "Süßdolde", "Süßholz", "Szechuanpfeffer", "Tamarinde", "Tanduri Masala", "Tasmanischer Bergpfeffer", "Tonkabohne", "Thymian", "Tripmadam", "Trüffel", "Tschubritza", "Vanille", "Wasabi", "Wacholder", "Waldmeister", "Wald-Weidenröschen", "Wasserpfeffer","Weinblätter", "Weinraute", "Ysop", "Zichorie", "Zimt", "Zitronengras", "Zitronenmelisse", "Zitronenthymian", "Zitwerwurzel", "Zucker"
        )

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
        val sommersalat = getImage(R.drawable.sommersalat)
        val recipes = ArrayList<DBRecipe>()
        recipes.add(DBRecipe(0, "Nudelauflauf mit Tomaten und Mozzarella", "Den Ofen auf 200 °C (Umluft 180 °C) vorheizen.\nDie Zwiebel und den Knoblauch sehr fein schneiden.Die Kirschtomaten waschen und halbieren. Den Parmesan reiben und den Mozzarella grob würfeln. Die Basilikumblätter abzupfen, waschen und trocken tupfen.\n\nIn einem großen Topf Salzwasser zum Kochen bringen und die Nudeln darin kochen. Währenddessen in einer großen Pfanne Olivenöl erhitzen und Zwiebel, Knoblauch anschwitzen.Die passierten Tomaten hinzufügen und die Sauce ein paar Minuten leicht köcheln lassen. Dann die Sahne und den geriebenen Parmesan unterrühren und die Sauce mit Salz, Pfeffer und einer ordentlichen Prise Zucker abschmecken.\n\nWenn die Nudeln soweit sind, diese abgießen und in die Pfanne zur Sauce geben. Die Pfanne von der Hitze nehmen und die halbierten Kirschtomaten und die Hälfte der Mozzarellawürfel unterheben. Die Basilikumblätter in Streifen schneiden und ebenfalls unterheben.\n\nAlles zusammen in eine Auflaufform geben, mit dem restlichen Mozzarella bestreuen und ca. 20 Minuten auf der mittleren Schiene im Backofen gratinieren.\n\nDazu passt zum Beispiel ein grüner Salat und Knoblauchbaguette.\n\nRezept von Katrinili @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Tomatensauce", "Den Stielansatz der Tomaten abschneiden, Die Tomaten halbieren und in eine Schüssel geben. Die Knoblauchzehen fein hacken und über die Tomaten streuen. 1 EL Olivenöl darüber träufeln. Für 4 Minuten bei 750 W in die Mikrowelle stellen, danach in einen Mixer umfüllen.\n Die Zwiebel klein hacken und mit 1 EL Olivenöl bei starker Hitze in einer kleinen Pfanne anbraten, bis sie leicht Farbe nimmt. Die Zwiebel zusammen mit den Basilikumblättern, dem Zucker, dem Tomatenmark und der Sahne ebenfalls in den Mixer geben und alles fein pürieren. Mit Salz abschmecken.\n Fertig ist eine leckere Tomatensauce, z. B. für Spaghetti Napoli oder als Grundlage für viele andere Rezepte mit Tomatensauce.\n\nRezept von Tickerix @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Pizza Mozzarella", "Zunächst für den Pizzateig Mehl, Grieß und Salz gründlich vermengen. Die Hefe im warmen Wasser auflösen, 5 Minuten ruhen lassen und dann zur Mehlmischung geben. Die Zutaten so lange mit dem Knethaken des Handrührers, in der Küchenmaschine oder von Hand kneten, bis ein elastischer Teig entsteht, das dauert ungefähr 10 Minuten. Falls der Teig zu fest sein sollte einfach noch etwas warmes Wasser zugeben, wenn der Teig zu flüssig ist, etwas Mehl hinzugeben. Erst dann das Olivenöl unterkneten.\n\nDen Teig in Frischhaltefolie wickeln oder unter einem Geschirrtuch mindestens 30 Minuten gehen lassen. Der Teig lässt sich ebenfalls hervorragend im Hefeteig-Programm eines Brotbackautomaten zubereiten.In der Zwischenzeit die Roma-Tomaten quer halbieren und über einer feinen Reibe bis auf die Schale abreiben. Das überschüssige Wasser aus den Tomaten durch ein feines Sieb abgießen, so dass nur der Tomatensaft und das Innere der Tomaten übrig bleiben. Diese Tomaten nach Geschmack mit etwas Salz würzen.\n Ein Backblech ordentlich mit gutem, erhitzbarem Olivenöl bepinseln und den Ofen auf 250°C Ober- und Unterhitze vorheizen. Den Teig nochmals von Hand durchkneten und auf einem bemehlten Brett etwa in Größe des Blechs von der Mitte nach außen ausrollen. Der Teig sollte dabei etwa 3 mm dick ausgerollt werden. Den ausgerollten Teig auf das Blech geben und nur hauchdünn (das ist wichtig!) mit den Tomaten bestreichen. Mit dem Oregano bestreuen. Den Mozzarella von Hand in Stücke reißen und über die Pizza streuen. \n\nAuf der zweiten Einschubleiste von unten etwa 10 Minuten backen. Wer einen Pizzastein hat, kann sich das Backen auf dem Blech natürlich sparen und stattdessen zwei Runde Pizzen aus dem Teig formen.\n\nRezept von moeyskitchen @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Pizza-Bällchen", "Mehl, Joghurt, Backpulver, Milch, Öl, Salz und Zucker gut verkneten. Die übrigen Zutaten zum Teig geben. Noch mal durchkneten und kleine Bällchen formen. Auf ein Backblech mit Backpapier legen und für 30 - 40 Min. bei 180 Grad in den Ofen.\n\nBeim Backen muss man hin und wieder gucken, sie werden je nach Herd von unten auch sehr schnell dunkel, aber zu hell sollten sie auch nicht sein. Vorsicht, wenn sie noch warm sind, werden sie gerne stibitzt. \n\nRezept von Sharly0 @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Frische Tomatensuppe", "Die Tomaten an der Unterseite kreuzweise anritzen und in eine große Schüssel legen. Mit kochendem oder heißem Wasser übergießen. Nach 10 Minuten vorsichtig die Haut und den grünen Knopf auf der Oberseite von der Tomate entfernen und die geschälten Tomaten klein schneiden.\n\nNun die Zwiebeln klein würfeln und den Knoblauch pressen. Beides in Olivenöl anbraten und nach ca. 3 Minuten die Tomatenstücke hinzugeben. Nach ein paar Minuten die Gemüsebrühe und die Gewürze (ohne die Petersilie) hinzugeben und auf niedriger Temperatur 20 Minuten kochen. Das Tomatenmark unterrühren und die Suppe mit einem Pürierstab fein pürieren.Nach Belieben mit den Gewürzen abschmecken und mit ein wenig frischer Petersilie servieren\n\nRezept von djbaraka @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Omas Gemüsesuppe", "Die Zwiebeln in Öl anrösten, Mehl dazugeben, unter Rühren rösten, bis die Mehlschwitze gelb ist. Mit ca. 1 Liter kaltem Wasser aufgießen. Mit dem Schneebesen schlagen, damit keine Klumpen entstehen.\nBrühwürfel, Salz und Pfeffer dazugeben und kräftig abschmecken. Gemüse und Kartoffeln klein geschnitten dazugeben und kochen, bis alles gar ist.\n\nRezept von a_cer @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Einfache Waffeln", "Margarine, Zucker, Eier schaumig schlagen. Dann Mehl, Backpulver und Milch hinzugeben und verrühren. Danach im Waffeleisen goldbraun backen.\n\nRezept von TBohrer @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Kartoffelsuppe", "Kartoffeln schälen und in kleine Stücke schneiden. Suppengemüse putzen und ebenfalls in kleine Stücke schneiden. Alles mit der Brühe in einen Topf geben und ca. 30 Minuten kochen. 5 Minuten vor Ende der Kochzeit die in kleine Stücke geschnittenen Würste und die Sahne dazugeben. Alles mit Salz, Pfeffer und Muskat abschmecken.\n\nRezept von motte1168 @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Einfaches Brot", "Alle Zutaten miteinander verkneten und abgedeckt auf doppelte Größe gehen lassen. Danach nochmals gründlich kneten und in eine gefettete Kastenform geben (ich streue noch zusätzlich Semmelbrösel hinein). Nochmals ca. 20 Minuten gehen lassen.\n\nDie Oberseite mit Wasser bestreichen. Dann ca. 40 - 50 Minuten bei 220 °C Ober-/Unterhitze im vorgeheizten Ofen backen.\n\nRezept von motte1168 @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Blumenkohlauflauf", "Den Blumenkohl in kochendem Salzwasser garen. In Röschen teilen und in eine gefettete Auflaufform geben.Die Butter in einem Topf zerlassen. Das Mehl zur Butter geben und etwas anschwitzen lassen. Die Milch und die Sahne dazugießen und unter ständigem Rühren bei mittlerer Hitze andicken lassen. Mit Muskat, Salz und Pfeffer abschmecken. Die Sauce nun über den Blumenkohl gießen. Mit Raspelkäse bestreuen und im vorgeheizten Backofen bei 180 °C Ober-/Unterhitze etwa 20 - 30 Minuten überbacken.\n\nRezept von Nicky0110 @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Pfannkuchen", "Das Mehl sieben. Die Milch mit Salz, Zucker, Vanillinzucker, Eiern verquirlen. Das Mehl zugeben und alles zu einem Teig verrühren. Die Butter zerlassen und zuletzt untermischen. Nun etwas Butter in einer Pfanne schmelzen und den Teig portionsweise hinein geben. Bei mittlerer Hitze von beiden Seiten goldbraun backen. \n\nRezept von StAn86 @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Pommes frites", "Die Kartoffeln waschen und gründlich trocknen. Der Länge nach erst in 1 cm dicke Scheiben, dann jede Scheibe in 1 cm breite Stäbe schneiden. Das Öl in einer tiefen, schweren Pfanne erhitzen. Die Kartoffelstäbchen in kleinen Portionen ins mäßig heiße Öl tauchen und 4 Minuten hellgelb frittieren. Die Pommes frites mit einer Zange behutsam aus dem Öl heben und auf Küchenpapier abtropfen lassen. Kurz vor dem Servieren das Öl noch einmal erhitzen. Die Pommes frites wiederum in Portionen knusprig und goldgelb frittieren. Auf Küchenpapier abtropfen lassen. Sofort auftragen. Die Frittierzeit beträgt 6 - 8 Minuten.\n\nRezept von Martina1976 @Chefkoch.de", placeholder))
        recipes.add(DBRecipe(0, "Frischer Sommersalat", "Das Gemüse waschen und danach nach belieben klein schneiden. Mozzarella abtropfen lassen und alles in eine Schüssel geben. \nJe nach belieben Zitronen auspressen und mit Olivenöl und etwas Essig abschmecken. \nSchon ist der Salat fertig!", sommersalat))
        recipes.add(DBRecipe(0, "Bratkartoffeln", "Hier mein Trick 17 - Supertrick für superknusprige Bratkartoffeln.\n\nKartoffeln schälen und in mind. 5 mm dicke Scheiben schneiden. Wasser in eine Pfanne geben und die Scheiben darauf verteilen. Nun solange auf höchster Stufe einköcheln lassen, bis das Wasser nicht mehr zu sehen ist.\nEine Scheibe probieren und wenn sie fast gar ist, die Hitze hochstellen und braten, bis die Kartoffeln eine schöne Farbe bekommen. Danach salzen und sofort servieren.\n\nRezept von Kaffeeluder @Chefkoch.de", placeholder))

        val contentValues = ContentValues()
        val quantitys = arrayListOf<DBQuantity>()
        quantitys.add(DBQuantity(1, 92, "400g", ""))
        quantitys.add(DBQuantity(1, 59, "2 Zehen", ""))
        quantitys.add(DBQuantity(1, 82, "1", ""))
        quantitys.add(DBQuantity(1, 76, "500g ", ""))
        quantitys.add(DBQuantity(1, 134, "250g ", ""))
        quantitys.add(DBQuantity(1, 101, "50g ", ""))
        quantitys.add(DBQuantity(1, 102, "125g ", ""))
        quantitys.add(DBQuantity(1, 135, "400g ", ""))
        quantitys.add(DBQuantity(1, 145, "1 Bund ", ""))
        quantitys.add(DBQuantity(1, 99, "", ""))
        quantitys.add(DBQuantity(1, 230, "", ""))
        quantitys.add(DBQuantity(1, 219, "", ""))
        quantitys.add(DBQuantity(1, 270, "", ""))
        quantitys.add(DBQuantity(2, 135, "500g", ""))
        quantitys.add(DBQuantity(2, 99, "2EL", ""))
        quantitys.add(DBQuantity(2, 59, "2 Zehen", ""))
        quantitys.add(DBQuantity(2, 82, "1/2", ""))
        quantitys.add(DBQuantity(2, 145, "5 Blätter", ""))
        quantitys.add(DBQuantity(2, 270, "2TL", ""))
        quantitys.add(DBQuantity(2, 136, "1EL", ""))
        quantitys.add(DBQuantity(2, 230, "", ""))
        quantitys.add(DBQuantity(2, 134, "40ml", ""))
        quantitys.add(DBQuantity(3, 137, "200g", ""))
        quantitys.add(DBQuantity(3, 131, "50g", ""))
        quantitys.add(DBQuantity(3, 230, "1TL", ""))
        quantitys.add(DBQuantity(3, 99, "2EL", ""))
        quantitys.add(DBQuantity(3, 76, "4", ""))
        quantitys.add(DBQuantity(3, 211, "1TL", ""))
        quantitys.add(DBQuantity(3, 102, "1 Kugel", ""))
        quantitys.add(DBQuantity(4, 137, "300g", ""))
        quantitys.add(DBQuantity(4, 111, "250g", ""))
        quantitys.add(DBQuantity(4, 96, "8EL", ""))
        quantitys.add(DBQuantity(4, 98, "6EL", ""))
        quantitys.add(DBQuantity(4, 230, "1TL", ""))
        quantitys.add(DBQuantity(4, 270, "1EL", ""))
        quantitys.add(DBQuantity(4, 82, "100g", ""))
        quantitys.add(DBQuantity(4, 101, "200g", ""))
        quantitys.add(DBQuantity(4, 109, "100g", ""))
        quantitys.add(DBQuantity(5, 76, "1kg", ""))
        quantitys.add(DBQuantity(5, 82, "2", ""))
        quantitys.add(DBQuantity(5, 59, "3 Zehen", ""))
        quantitys.add(DBQuantity(5, 99, "", ""))
        quantitys.add(DBQuantity(5, 218, "", ""))
        quantitys.add(DBQuantity(5, 251, "", ""))
        quantitys.add(DBQuantity(5, 136, "3TL", ""))
        quantitys.add(DBQuantity(5, 216, "", ""))
        quantitys.add(DBQuantity(6, 82, "1", ""))
        quantitys.add(DBQuantity(6, 52, "1/4", ""))
        quantitys.add(DBQuantity(6, 137, "2EL", ""))
        quantitys.add(DBQuantity(6, 98, "", ""))
        quantitys.add(DBQuantity(6, 230, "", ""))
        quantitys.add(DBQuantity(6, 219, "", ""))
        quantitys.add(DBQuantity(6, 58, "3", ""))
        quantitys.add(DBQuantity(6, 54, "100g", ""))
        quantitys.add(DBQuantity(7, 138, "250g", ""))
        quantitys.add(DBQuantity(7, 270, "200g", ""))
        quantitys.add(DBQuantity(7, 110, "2", ""))
        quantitys.add(DBQuantity(7, 137, "500g", ""))
        quantitys.add(DBQuantity(7, 96, "500ml", ""))
        quantitys.add(DBQuantity(8, 58, "1kg", ""))
        quantitys.add(DBQuantity(8, 134, "100g", ""))
        quantitys.add(DBQuantity(8, 230, "", ""))
        quantitys.add(DBQuantity(8, 291, "", ""))
        quantitys.add(DBQuantity(8, 209, "500ml", ""))
        quantitys.add(DBQuantity(9, 137, "500g", ""))
        quantitys.add(DBQuantity(9, 270, "1EL", ""))
        quantitys.add(DBQuantity(9, 230, "1TL", ""))
        quantitys.add(DBQuantity(9, 98, "2EL", ""))
        quantitys.add(DBQuantity(10, 52, "1Kopf", ""))
        quantitys.add(DBQuantity(10, 138, "2EL", ""))
        quantitys.add(DBQuantity(10, 96, "200ml", ""))
        quantitys.add(DBQuantity(10, 137, "1EL", ""))
        quantitys.add(DBQuantity(10, 134, "200ml", ""))
        quantitys.add(DBQuantity(10, 209, "Prise", ""))
        quantitys.add(DBQuantity(10, 230, "", ""))
        quantitys.add(DBQuantity(10, 219, "", ""))
        quantitys.add(DBQuantity(11, 137, "100g", ""))
        quantitys.add(DBQuantity(11, 96, "250ml", ""))
        quantitys.add(DBQuantity(11, 230, "Prise", ""))
        quantitys.add(DBQuantity(11, 270, "2EL", ""))
        quantitys.add(DBQuantity(11, 110, "3", ""))
        quantitys.add(DBQuantity(11, 138, "50g", ""))
        quantitys.add(DBQuantity(12, 58, "6", ""))
        quantitys.add(DBQuantity(12, 98, "", ""))
        quantitys.add(DBQuantity(13, 76, "3", ""))
        quantitys.add(DBQuantity(13, 102, "2Kugeln", ""))
        quantitys.add(DBQuantity(13, 230, "", ""))
        quantitys.add(DBQuantity(13, 216, "", ""))
        quantitys.add(DBQuantity(13, 47, "1", ""))
        quantitys.add(DBQuantity(13, 99, "", ""))
        quantitys.add(DBQuantity(14, 58, "5-6", ""))
        quantitys.add(DBQuantity(14, 138, "", ""))
        quantitys.add(DBQuantity(14, 98, "1EL", ""))
        quantitys.add(DBQuantity(14, 230, "", ""))
        quantitys.add(DBQuantity(15, 265, "", ""))
        quantitys.add(DBQuantity(15, 265, "", ""))
        quantitys.add(DBQuantity(15, 265, "", ""))
        quantitys.add(DBQuantity(15, 265, "", ""))
        quantitys.add(DBQuantity(15, 265, "", ""))
        quantitys.add(DBQuantity(15, 265, "", ""))

        for (i in 0 until recipes.size){
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