package com.mycompany.advioo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.R
import com.mycompany.advioo.models.campaign.Campaign
import javax.inject.Inject

class HomeFeedAdapter @Inject constructor(
    val glide : RequestManager
) : RecyclerView.Adapter<HomeFeedAdapter.CampaignViewHolder>(){

    class CampaignViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private val diffUtil = object : DiffUtil.ItemCallback<Campaign>(){
        override fun areItemsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)

    var campaigns: List<Campaign>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFeedAdapter.CampaignViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_ad_item_row,parent,false)
        return CampaignViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeFeedAdapter.CampaignViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return campaigns.size
    }

}