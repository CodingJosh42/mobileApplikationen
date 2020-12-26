package com.example.mobappproject.dataClasses

import java.util.*


data class Ingredient(val name: String) {
    override fun equals(other: Any?): Boolean {
        if(other != null && other is Ingredient)
            return other.name.equals(name, ignoreCase = true)
        return false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
