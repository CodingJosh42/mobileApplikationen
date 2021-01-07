package com.example.mobappproject.database

class DBRecipe(val id: Int, val name: String, val description: String, val picture: String): Comparable<DBRecipe> {
    var imgId: Int ?= null
    var quantitys: ArrayList<DBQuantity> ?= null
    var matches: Int ?= null

    override fun compareTo(other: DBRecipe): Int {
        val percent = matches?.div(quantitys!!.size.toFloat())
        val percentOther = other.matches?.div(quantitys!!.size.toFloat())
        if (percent != null && percentOther != null) {
            return when {
                percent > percentOther -> {
                    1
                }
                percent < percentOther -> {
                    -1
                }
                else -> {
                    0
                }
            }
        }
        return 0
    }
}