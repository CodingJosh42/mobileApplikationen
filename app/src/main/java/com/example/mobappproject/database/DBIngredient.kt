package com.example.mobappproject.database

class DBIngredient(val id: Int, val name: String, val spice : Int, var stored: Int) {

    override fun toString(): String {
        return this.name
    }
}