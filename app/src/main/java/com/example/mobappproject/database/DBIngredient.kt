package com.example.mobappproject.database

import com.example.mobappproject.dataClasses.Ingredient
import java.io.Serializable

class DBIngredient(val id: Int, val name: String, val spice : Int, var stored: Int) : Serializable{

    override fun toString(): String {
        return this.name
    }

    override fun equals(other: Any?): Boolean {
        if(other != null && other is DBIngredient)
            return other.name.equals(name, ignoreCase = true)
        return false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}