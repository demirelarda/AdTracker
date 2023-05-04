package com.mycompany.advioo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mycompany.advioo.R
import com.mycompany.advioo.models.city.Province
import javax.inject.Inject

class StateListAdapter @Inject constructor() : RecyclerView.Adapter<StateListAdapter.StateHolder>() {

    class StateHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((Province) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<Province>() {
        override fun areItemsTheSame(oldItem: Province, newItem: Province): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Province, newItem: Province): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var states: List<Province>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    fun setOnItemClickListener(listener: (Province) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.city_list_row, parent, false)
        return StateHolder(view)
    }

    override fun onBindViewHolder(holder: StateHolder, position: Int) {
        val model = states[position]
        val tvCity = holder.itemView.findViewById<TextView>(R.id.row_tv_cityName)
        tvCity.text = model.stateName
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(model)
        }
    }

    override fun getItemCount(): Int {
        return states.size
    }
}