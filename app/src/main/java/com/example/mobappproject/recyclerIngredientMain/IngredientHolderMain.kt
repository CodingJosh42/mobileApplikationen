package com.example.mobappproject.recyclerIngredientMain

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.dataClasses.Ingredient

class IngredientHolderMain(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_ingredient_main_activity, parent, false)) {
        private var mTitleView: TextView? = null
        private var button: ImageButton? = null


        init {
            mTitleView = itemView.findViewById<TextView>(R.id.textView1)
            button = itemView.findViewById<ImageButton>(R.id.remove)
        }

        fun bind(ingredient: Ingredient) {
            mTitleView?.text = ingredient.name
        }

        fun getButton(): ImageButton? {
            return button
        }
}