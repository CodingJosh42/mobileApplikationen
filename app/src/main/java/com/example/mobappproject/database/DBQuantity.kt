package com.example.mobappproject.database


class DBQuantity(var recipe_id:Int, val ingredient_id: Int, val quantity: String, val ingredientName: String) {
    var ingredient: DBIngredient ?= null
}
