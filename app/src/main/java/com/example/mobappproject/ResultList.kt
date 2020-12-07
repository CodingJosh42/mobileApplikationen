package com.example.mobappproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class ResultList : AppCompatActivity() {

    private var inflater: LayoutInflater? = null
    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)
        print("Until here")
        mContext = this
        inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        var layout : LinearLayout  = findViewById(R.id.linearlayout1)
        print("Until here 2")
        addView(layout)
    }

    fun addView(view: LinearLayout) {
        for (i in 0..5) {
            var listItem = inflater?.inflate(R.layout.layout_list_item, null)
            view.addView(listItem)
        }
    }

}