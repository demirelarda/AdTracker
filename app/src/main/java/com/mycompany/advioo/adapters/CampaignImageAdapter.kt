package com.mycompany.advioo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.R

class CampaignImageAdapter(
    private val glide: RequestManager,
    private val images: List<Any>
) : RecyclerView.Adapter<CampaignImageAdapter.CampaignImageViewHolder>() {

    var onPageClick: ((Int) -> Unit)? = null

    inner class CampaignImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignImageViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_pager_item, parent, false) as ImageView

        return CampaignImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: CampaignImageViewHolder, position: Int) {
        val item = images[position]
        when (item) {
            is Int -> holder.imageView.setImageResource(item)
            is String -> glide.load(item).into(holder.imageView)
        }
    }

    override fun getItemCount() = images.size
}
