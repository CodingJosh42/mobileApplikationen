package com.example.mobappproject.activities

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobappproject.R
import com.example.mobappproject.database.DBIngredient
import com.example.mobappproject.database.DatabaseHandler
import com.example.mobappproject.arrayListAdapter.ArrayListAdapter
import com.example.mobappproject.recycleViewIngredients.RecyclerAdapter
import com.example.mobappproject.recycleViewIngredients.SwipeCallback

/**
 * ListFragment. Used for ingredient and SpiceList
 */
class ListFragment : Fragment() {

    private var linearLayoutManager: LinearLayoutManager? = null
    var recyclerView: RecyclerView? = null
    private lateinit var db: DatabaseHandler
    private val mIngredients = ArrayList<DBIngredient>()
    private val availableIngredients = ArrayList<DBIngredient>()
    private var arrayListAdapter: ArrayListAdapter?= null
    private var isSpice: Int? = null
    private var input: AutoCompleteTextView ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isSpice = it.getInt("spice")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = DatabaseHandler(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_list, container, false)
        input = v.findViewById(R.id.input)

        loadIngredients()

        setUpInput(v)

        setRecycler(v)

        return v
    }
    /**
     * Sets up the AutoCompleteTextView. Adds onClickListener, setOnEditorActionListener to input.
     * Initializes the arrayListAdapter and adds it to input
     */
    private fun setUpInput(v: View) {
        val addInput = v.findViewById<Button>(R.id.addInput)
        addInput.setOnClickListener {
            addIngredient(v)
        }

        val input = v.findViewById<AutoCompleteTextView>(R.id.input)
        input.setOnEditorActionListener(TextView.OnEditorActionListener { view, keyCode, event ->
            var handled = false
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                addIngredient(v)
                handled = true
            } else if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
                addIngredient(v)
                handled = true
            }
            if (handled) {
                val focus = activity?.currentFocus
                if (focus != null) {
                    val imm: InputMethodManager = activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)  as InputMethodManager
                    imm.hideSoftInputFromWindow(focus.windowToken, 0)
                }
            }
            return@OnEditorActionListener handled
        })

        this.arrayListAdapter = ArrayListAdapter(requireActivity(),
            R.layout.simple_dropdown_item_1line, availableIngredients)
        input.threshold = 1
        input.setAdapter(arrayListAdapter)

        if(isSpice == 0) {
            input.hint = "Zutat hinzufügen"
        } else {
            input.hint = "Gewürz hinzufügen"
        }
    }

    /**
     * Sets up the Recycler. setUpInput must be called before this to initialize the arrayListAdapter
     */
    private fun setRecycler(v: View) {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView = v.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        val adapter = RecyclerAdapter(mIngredients, arrayListAdapter!!, db)
        recyclerView?.adapter = adapter
        val itemTouch = ItemTouchHelper(SwipeCallback(adapter))
        itemTouch.attachToRecyclerView(recyclerView)
    }

    /**
     * Called when the user wants to add an Ingredient to his List
     * Adds the ingredient to the user list and removes it from the availableList
     */
    private fun addIngredient(v: View){
        val text = input?.text.toString()
        if(text != "") {
            val fakeIng = DBIngredient(0,text,0,0)
            if(arrayListAdapter?.contains(fakeIng) == true) {
                val index = arrayListAdapter?.indexOf(fakeIng) as Int
                val ing = arrayListAdapter?.get(index) as DBIngredient

                if (db.addStoreIngredient(ing) > -1 ) {
                    arrayListAdapter?.remove(ing)
                    arrayListAdapter?.notifyDataSetChanged()
                    ing.stored = 1
                    mIngredients.add(ing)

                    recyclerView?.adapter?.notifyDataSetChanged()
                    recyclerView?.scrollToPosition(mIngredients.size - 1)
                    val msg = Toast.makeText(activity, text + " hinzugefügt", Toast.LENGTH_SHORT)
                    msg.show()
                    input?.text?.clear()
                } else {
                    val msg = Toast.makeText(activity, text + " konnte nicht abgespeichert werden", Toast.LENGTH_SHORT)
                    msg.show()
                }
            } else if(mIngredients.contains(fakeIng)) {
                val msg = Toast.makeText(activity, text + " ist bereits in deiner Liste", Toast.LENGTH_SHORT)
                msg.show()
            } else  {
                var toPrint = " ist keine valide Zutat"
                if(isSpice == 1)
                    toPrint = " ist kein valides Gewürz"
                val msg = Toast.makeText(activity, text + toPrint, Toast.LENGTH_SHORT)
                msg.show()
            }
        }
    }


    /**
     * Loads all Ingredients from the database. Adds ingredients either to the users ingredientList
     * or to the available IngredientList
     */
    private fun loadIngredients() {
        val dbIngs = db.getIngredients(isSpice as Int)
        for (item in dbIngs){
            if(item.stored == 1 && !mIngredients.contains(item)) {
                mIngredients.add(item)
            } else if(!availableIngredients.contains(item)){
                availableIngredients.add(item)
            }
        }
    }



    companion object {
        /**
         * Creates a new Instance of ListFragment
         * @param spice 0 if IngredientList, 1 if SpiceList should be displayed
         */
        @JvmStatic
        fun newInstance(spice: Int) =
                ListFragment().apply {
                    arguments = Bundle().apply {
                        putInt("spice", spice)
                    }
                }
    }
}