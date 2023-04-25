package com.mycompany.advioo.adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.R
import com.mycompany.advioo.models.campaign.Campaign
import javax.inject.Inject

class HomeFeedAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<HomeFeedAdapter.CampaignViewHolder>() {

    class CampaignViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((Campaign) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<Campaign>(){
        override fun areItemsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickListener(listener: (Campaign) -> Unit) {
        onItemClickListener = listener
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
        val model = campaigns[position]
        val tvCampaignPrice = holder.itemView.findViewById<TextView>(R.id.tv_campaign_price_row)
        holder.itemView.apply {
            tvCampaignPrice.text = model.campaignPaymentPerKM.toString()
        }
        val tvCampaignTitle = holder.itemView.findViewById<TextView>(R.id.tv_campaign_title_home_row)
        holder.itemView.apply {
            tvCampaignTitle.text = model.campaignTitle
        }

        val campaignImage = holder.itemView.findViewById<ImageView>(R.id.iv_home_campaign_row)
        glide.load(model.campaignImageURL).into(campaignImage)

        holder.itemView.setOnClickListener {
            onItemClickListener!!.invoke(model)
        }
    }

    override fun getItemCount(): Int {
        return campaigns.size
    }

}