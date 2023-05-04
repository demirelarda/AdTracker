package com.mycompany.advioo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mycompany.advioo.R
import com.mycompany.advioo.models.installer.Installer
import javax.inject.Inject

class InstallerListAdapter @Inject constructor() : RecyclerView.Adapter<InstallerListAdapter.InstallerViewHolder>() {


    class InstallerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((Installer) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<Installer>() {
        override fun areItemsTheSame(oldItem: Installer, newItem: Installer): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Installer, newItem: Installer): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var installers: List<Installer>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    fun setOnItemClickListener(listener: (Installer) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.installer_list_row, parent, false)
        return InstallerViewHolder(view)
    }


    override fun onBindViewHolder(holder: InstallerViewHolder, position: Int) {
        val model = installers[position]
        val tvInstallerName = holder.itemView.findViewById<TextView>(R.id.row_tv_installer_name)
        tvInstallerName.text = model.installerName
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(model)
        }
    }

    override fun getItemCount(): Int {
        println("size = "+installers.size)
        return installers.size
    }


}