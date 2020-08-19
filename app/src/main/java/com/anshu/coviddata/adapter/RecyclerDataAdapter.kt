package com.anshu.coviddata.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anshu.coviddata.R
import com.anshu.coviddata.model.data
import java.lang.Double.parseDouble

class RecyclerDataAdapter(val context: Context, var cartItems:ArrayList<data>): RecyclerView.Adapter<RecyclerDataAdapter.ViewHolderData>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_data_single_row,parent,false)

        return ViewHolderData(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val cartItemObject = cartItems[position]


            holder.txtNameOfTheState.text = cartItemObject.loc
            holder.txtTotalCases.text = cartItemObject.totalConfirmed
            holder.txtRecoveredCases.text = cartItemObject.discharged
            holder.txtDeaths.text = cartItemObject.deaths



    }

    fun filterList(filteredList: ArrayList<data>) {//to update the recycler view depending on the search
        cartItems = filteredList
        notifyDataSetChanged()
    }


    class ViewHolderData(view: View): RecyclerView.ViewHolder(view){
       val txtNameOfTheState: TextView = view.findViewById(R.id.txtNameOfTheState)
        val txtTotalCases: TextView = view.findViewById(R.id.txtTotalCases)
        val txtRecoveredCases: TextView = view.findViewById(R.id.txtRecoveredCases)
        val txtDeaths: TextView = view.findViewById(R.id.txtDeaths)


    }

}