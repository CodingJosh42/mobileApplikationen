package com.example.mobappproject

class RestDummy {
    fun getRecipes():  List<Recipe> {
        var list = ArrayList<Recipe>()
        val ing = ArrayList<String>()
        ing.add("Käse")
        ing.add("Tomaten")
        ing.add("Hefe")
        ing.add("Mehl")
        val matches = ArrayList<String>()
        matches.add("Käse")
        matches.add("Tomaten")
        matches.add("Hefe")
        matches.add("Mehl")
        list.add(Recipe(0,"Pizza Margherita", ing, "placeholder", matches))
        list.add(Recipe(1,"Pizza1", ing, "placeholder", matches))
        list.add(Recipe(2,"Pizza2", ing, "placeholder", matches))
        list.add(Recipe(3,"Pizza3", ing, "placeholder", matches))
        list.add(Recipe(4,"Pizza4", ing, "placeholder", matches))
        list.add(Recipe(5,"Pizza5", ing, "placeholder", matches))
        return list
    }

    fun getRecipe(id: Int): Recipe {
        val ing = ArrayList<String>()
        ing.add("Käse")
        ing.add("Tomaten")
        ing.add("Hefe")
        ing.add("Mehl")
        return Recipe(id, "Pizza Margherita", ing, "placeholder", "Zuerst wird der Teig hergestellt. Teig 30 Minuten lang gehen lassen. Pizza belegen und 15 Minuten im Ofen backen" )
    }
}