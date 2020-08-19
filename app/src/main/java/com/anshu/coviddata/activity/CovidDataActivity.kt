package com.anshu.coviddata.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.anshu.coviddata.R
import com.anshu.coviddata.adapter.RecyclerDataAdapter
import com.anshu.coviddata.model.data
import com.anshu.coviddata.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class CovidDataActivity : AppCompatActivity() {


    lateinit var toolBar: Toolbar
    lateinit var recyclerData: RecyclerView
    lateinit var recylerAdapter: RecyclerDataAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var editTextSearch: EditText
    lateinit var radioButtonView:View
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var dashboard_fragment_cant_find_restaurant:RelativeLayout
    lateinit var sharedPreferences: SharedPreferences

    var costComparator= Comparator<data> { rest1, rest2 ->

        rest1.totalConfirmed.compareTo(rest2.totalConfirmed,true)

    }

    var deathComparator= Comparator<data> { rest1, rest2 ->

        rest1.deaths.compareTo(rest2.deaths,true)

    }

    var dataInfoList= arrayListOf<data>()
//    var totalInfoList= arrayListOf<Total>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covid_data)

        toolBar = findViewById(R.id.toolBar)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        layoutManager= LinearLayoutManager(this as Context)
        recyclerData =findViewById(R.id.recyclerData)
        editTextSearch=findViewById(R.id.editTextSearch)
        dashboard_fragment_cant_find_restaurant= findViewById(R.id.dashboard_fragment_cant_find_restaurant)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        setToolBar()
        progressLayout.visibility = View.VISIBLE


        fun filterFun(strTyped:String){//to filter the recycler view depending on what is typed
            val filteredList= arrayListOf<data>()

            for (item in dataInfoList){
                if(item.loc.toLowerCase().contains(strTyped.toLowerCase())){//to ignore case and if contained add to new list

                    filteredList.add(item)

                }
            }

            if(filteredList.size==0){
                dashboard_fragment_cant_find_restaurant.visibility=View.VISIBLE
            }
            else{
                dashboard_fragment_cant_find_restaurant.visibility=View.INVISIBLE
            }

            recylerAdapter.filterList(filteredList)

        }
        editTextSearch.addTextChangedListener(object : TextWatcher {//as the user types the search filter is applied
        override fun afterTextChanged(strTyped: Editable?) {
            filterFun(strTyped.toString())
        }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )


    }
    fun fetch() {


        if (ConnectionManager().checkConnectivity(this as Context)) {

                progressLayout.visibility = View.GONE
            try {

                val queue = Volley.newRequestQueue(this as Context)


                //val restaurantId:String=""

                val url = "https://api.rootnet.in/covid19-in/stats/latest"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        println("Response12menu is $it")

                            val success = it.getBoolean("success")
                        if(success)
                        {
                            val dataObject = it.getJSONObject("data")
                            val totalCases =dataObject.getJSONObject("summary")
                            sharedPreferences.edit().putString("total_cases",totalCases.getString("total")).apply()
                            sharedPreferences.edit().putString("total_indian_case",totalCases.getString("confirmedCasesIndian")).apply()
                            sharedPreferences.edit().putString("total_foreign_case",totalCases.getString("confirmedCasesForeign")).apply()
                            sharedPreferences.edit().putString("total_discharged",totalCases.getString("discharged")).apply()
                            sharedPreferences.edit().putString("total_deaths",totalCases.getString("deaths")).apply()




                            val regionalObject = dataObject.getJSONArray("regional")

                            for (i in 0 until regionalObject.length()) {
                                val bookJsonObject = regionalObject.getJSONObject(i)
                                val menuObject = data(
                                bookJsonObject.getString("loc"),
                                bookJsonObject.getString("totalConfirmed"),

                                bookJsonObject.getString("discharged"),
                                    bookJsonObject.getString("deaths")



                                    )
                                dataInfoList.add(menuObject)

                                //progressBar.visibility = View.GONE

                                recylerAdapter = RecyclerDataAdapter(
                                    this as Context,
                                    dataInfoList


                                )//set the adapter with the data

                                recyclerData.adapter =
                                    recylerAdapter//bind the  recyclerView to the adapter

                                recyclerData.layoutManager =
                                    layoutManager //bind the  recyclerView to the layoutManager

                            }

                        }
//                            progressBar.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        println("Error12menu is " + it)

                        Toast.makeText(
                            this as Context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

//                            progressBar.visibility = View.INVISIBLE
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
//                        headers["x-rapidapi-key"] = "5d8f8b8f4emshd478b2356a3dbbap16f0edjsne3817dfa03ed"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    this as Context,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                finishAffinity()//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()
        }

    }


    fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Covid Data"
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)//change icon to custom
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater?.inflate(R.menu.menu_dashboard,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){

            R.id.sort->{
                radioButtonView= View.inflate(this,R.layout.sort_radio_button,null)//radiobutton view for sorting display
                androidx.appcompat.app.AlertDialog.Builder(this )
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(dataInfoList, costComparator)
                            dataInfoList.reverse()
                            recylerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(dataInfoList, costComparator)
                            recylerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(dataInfoList, deathComparator)
                            recylerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                    }
                    .setNegativeButton("CANCEL") { text, listener ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        super.onBackPressed()
        val intent = Intent(this, TotalCases::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this as Context)) {
            if(dataInfoList.isEmpty())
                fetch()//if internet is available fetch data
        }else
        {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                finishAffinity()//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }


        super.onResume()
    }
}
