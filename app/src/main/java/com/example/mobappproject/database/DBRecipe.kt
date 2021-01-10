package com.example.mobappproject.database

import android.graphics.Bitmap

/**
 * Recipe class
 * @param id Id of recipe
 * @param name Name of recipe
 * @param description Preparation of recipe
 * @param picture Bitmap of recipe picture
 */
class DBRecipe(val id: Int, val name: String, val description: String, val picture: Bitmap?): Comparable<DBRecipe> {
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