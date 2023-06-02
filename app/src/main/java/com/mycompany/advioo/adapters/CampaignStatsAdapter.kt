package com.mycompany.advioo.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mycompany.advioo.R
import com.mycompany.advioo.models.CampaignStats
import com.mycompany.advioo.ui.fragments.CampaignStatsFragment

class CampaignStatsAdapter(
    private val campaignHistoryList: ArrayList<Triple<String,String,String>>
) : RecyclerView.Adapter<CampaignStatsAdapter.CampaignStatsViewHolder>(){

    inner class CampaignStatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignStatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_pager_item_campaign_stats,parent,false)
        return CampaignStatsViewHolder(view)
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
