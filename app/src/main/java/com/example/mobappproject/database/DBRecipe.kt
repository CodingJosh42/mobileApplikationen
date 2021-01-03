package com.example.mobappproject.database

import com.example.mobappproject.dataClasses.Ingredient

class DBRecipe(val id: Int, val name: String, val description: String, val picture: String, val ingredients: List<DBQuantity>?)