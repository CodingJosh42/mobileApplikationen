package com.example.mobappproject.database

import java.io.Serializable


class DBRecipe(val id: Int, val name: String, val description: String, val picture: String) {
    var imgId: Int ?= null
    var quantitys: ArrayList<DBQuantity> ?= null
}