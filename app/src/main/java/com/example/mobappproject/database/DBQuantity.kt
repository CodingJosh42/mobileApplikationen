package com.example.mobappproject.database

/**
 * Quantity class
 * @param recipe_id Id of recipe
 * @param ingredient_id Id of ingredient
 * @param quantity Quantity of ingredient
 * @param ingredientName Name of ingredient
 */
class DBQuantity(var recipe_id:Int, val ingredient_id: Int, val quantity: String, val ingredientName: String) {
    var ingredient: DBIngredient ?= null
}
