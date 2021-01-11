package com.example.mobappproject.database

import java.io.Serializable

/**
 * Ingredient class
 * @param id Id of ingredient
 * @param name Name of ingredient
 * @param spice if ingredient is a spice = 1, if not 0
 * @param stored if ingredient is stored = 1, if not 0
 */
class DBIngredient(val id: Int, val name: String, val spice: Int, var stored: Int) : Serializable {

    override fun toString(): String {
        return this.name
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is DBIngredient)
            return other.name.equals(name, ignoreCase = true)
        return false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}