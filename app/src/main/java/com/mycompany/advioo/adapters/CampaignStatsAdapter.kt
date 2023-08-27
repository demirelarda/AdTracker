package com.mycompany.advioo.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mycompany.advioo.R
import javax.inject.Inject

class CampaignStatsAdapter @Inject constructor() : RecyclerView.Adapter<CampaignStatsAdapter.CampaignStatsViewHolder>(){

    inner class CampaignStatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignStatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_pager_item_campaign_stats,parent,false)
        return CampaignStatsViewHolder(view)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Triple<String,String,String>>(){
        override fun areItemsTheSame(oldItem: Triple<String,String,String>, newItem: Triple<String,String,String>): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Triple<String,String,String>, newItem: Triple<String,String,String>): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)

    var campaignHistoryList: List<Triple<String,String,String>>
        get() = recyclerListDiffer.currentList
        set(value) {
            recyclerListDiffer.submitList(value)
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int {
        return campaignHistoryList.size
    }

    override fun onBindViewHolder(holder: CampaignStatsViewHolder, position: Int) {
        val currentCampaignHistory = campaignHistoryList[position]
        val timePeriod = holder.itemView.findViewById<TextView>(R.id.tv_time_period_campaign_stats)
        val priceText = holder.itemView.findViewById<TextView>(R.id.tv_payment_campaign_stats)
        val distanceText = holder.itemView.findViewById<TextView>(R.id.tv_distance_driven_campaign_stats)
        timePeriod.text = currentCampaignHistory.first
        priceText.text = currentCampaignHistory.second
        distanceText.text = currentCampaignHistory.third
        if(priceText.text.toString().length in 7..9){
            priceText.textSize = 60F
        }
        else if(priceText.text.toString().length in 10..20){
            priceText.textSize = 50F
        }
    }
}
