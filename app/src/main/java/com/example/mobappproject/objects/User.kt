package com.example.mobappproject.objects

import com.example.mobappproject.dataClasses.Ingredient

object User {

    var ingredients = ArrayList<Ingredient>()
    var spices = ArrayList<Ingredient>()

    /**
     * Loading ingredients from Database
     * NOT FINISHED
     */
    fun loadIngredients(){
        ingredients.addAll(arrayListOf(
                Ingredient("Gurke"),
                Ingredient("Tomate"),
                Ingredient("Käse"),
                Ingredient("Hefe"),
                Ingredient("Mehl"),
                Ingredient("Butter"),
                Ingredient("Öl"),
                Ingredient("Milch"),
                Ingredient("Schinken"))
        )
    }

    /**
     * Loading spices from Database
     * NOT FINISHED
     */
    fun loadSpices(){
        spices.addAll(arrayListOf(
                Ingredient("Pfeffer"),
                Ingredient("Salz"),
                Ingredient("Zimt"),
                Ingredient("Nelken"),
                Ingredient("Chilli"),
                Ingredient("Paprikapulver"))
        )
    }

    /**
     *
     */
    fun addIngredient(ingredient: Ingredient){
        ingredients.add(ingredient)
    }

}