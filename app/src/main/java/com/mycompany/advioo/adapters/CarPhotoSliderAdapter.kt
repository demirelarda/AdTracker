package com.mycompany.advioo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.R
import java.io.File

class CarPhotoSliderAdapter(
    private val glide: RequestManager,
    private val images: List<Any>
) : RecyclerView.Adapter<CarPhotoSliderAdapter.CarImageViewHolder>() {

    var onPageClick: ((Int) -> Unit)? = null

    inner class CarImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarImageViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_pager_item_user_car_photos, parent, false) as ImageView

        return CarImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: CarImageViewHolder, position: Int) {
        val item = images[position]
        when (item) {
            is Int -> holder.imageView.setImageResource(item)
            is String -> {
                val file = File(holder.imageView.context.filesDir, item)
                glide.load(file).into(holder.imageView)
            }
        }
    }


    override fun getItemCount() = images.size

}