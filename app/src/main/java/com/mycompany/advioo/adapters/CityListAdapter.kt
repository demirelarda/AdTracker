package com.mycompany.advioo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mycompany.advioo.R
import com.mycompany.advioo.models.city.City
import com.mycompany.advioo.models.city.Province
import javax.inject.Inject

class CityListAdapter @Inject constructor() : RecyclerView.Adapter<CityListAdapter.CityHolder>(){

    class CityHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((City) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var cities: List<City>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    fun setOnItemClickListener(listener: (City) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListAdapter.CityHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.city_list_row,parent,false)
        return CityHolder(view)
    }

    override fun onBindViewHolder(holder: CityListAdapter.CityHolder, position: Int) {
        val model = cities[position]
        val tvCity = holder.itemView.findViewById<TextView>(R.id.row_tv_cityName)
        holder.itemView.apply {
            tvCity.text = model.name
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(model)
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }


}