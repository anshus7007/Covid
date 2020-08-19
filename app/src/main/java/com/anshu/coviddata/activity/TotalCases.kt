package com.anshu.coviddata.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.anshu.coviddata.R

class TotalCases : AppCompatActivity() {

    lateinit var txtTotalCase: TextView
    lateinit var txtTotalIndianCase: TextView
    lateinit var txtTotalForeignCase: TextView

    lateinit var txtTotalRecoveredCases: TextView
    lateinit var txtTotalDeathCases: TextView
    lateinit var btnProceed: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_cases)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        txtTotalCase = findViewById(R.id.txtTotalCase)
        txtTotalIndianCase = findViewById(R.id.txtTotalIndianCase)
        txtTotalForeignCase = findViewById(R.id.txtTotalForeignCase)
        txtTotalRecoveredCases = findViewById(R.id.txtTotalRecoveredCases)
        txtTotalDeathCases = findViewById(R.id.txtTotalDeathCases)
        btnProceed = findViewById(R.id.btnProceed)

        txtTotalCase.text = sharedPreferences.getString("total_cases","")
        txtTotalIndianCase.text = sharedPreferences.getString("total_indian_case","")
        txtTotalForeignCase.text = sharedPreferences.getString("total_foreign_case","")
        txtTotalRecoveredCases.text = sharedPreferences.getString("total_discharged","")
        txtTotalDeathCases.text = sharedPreferences.getString("total_deaths","")
        btnProceed.setOnClickListener {
            val intent = Intent(this, CovidDataActivity::class.java)
            startActivity(intent)
        }

    }
}
