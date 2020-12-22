package com.example.mobappproject.dataClasses

class Recipe {
    var id: Int = 0
    var title: String = ""
    var ingredients = ArrayList<String>()
    var img: String = ""
    var matches = ArrayList<String>()
    var preparation = ""

    constructor(id: Int, title: String, ingredients: ArrayList<String>, img: String, matches: ArrayList<String>) {
        this.id = id
        this.title = title
        this.ingredients = ingredients
        this.img = img
        this.matches = matches
    }
    constructor(id: Int, title: String, ingredients: ArrayList<String>, img: String, preparation: String) {
        this.id = id
        this.title = title
        this.ingredients = ingredients
        this.img = img
        this.preparation = preparation
    }
}