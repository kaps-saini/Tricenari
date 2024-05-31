package com.mavalore.tricenari.adapter.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mavalore.tricenari.R
import com.mavalore.tricenari.domain.models.productRecomendation.RecommendedItemsData

class ProductRecommendationAdapter:RecyclerView.Adapter<ProductRecommendationAdapter.HorizontalViewHolder>() {

    inner class HorizontalViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.ivItemImage)
        private val itemName: TextView = itemView.findViewById(R.id.tvItemName)

        fun bind(recommendedItemsData: RecommendedItemsData){
            Glide.with(itemView.context)
                .load("https://www.tricenari.com/assets/img/product_images/${recommendedItemsData.id}.jpg")
                .into(itemImage)
            itemName.text = recommendedItemsData.displaytext
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, dataItem:Long, itemData: RecommendedItemsData)
    }

    private var onItemClickListener:OnClickListener? = null

    fun setOnItemClickListener(listener:OnClickListener){
        onItemClickListener = listener
    }

    //object for difference callback
    private val diffUtil = object : DiffUtil.ItemCallback<RecommendedItemsData>(){
        override fun areItemsTheSame(oldItem: RecommendedItemsData, newItem: RecommendedItemsData): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: RecommendedItemsData, newItem: RecommendedItemsData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        return HorizontalViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.single_item_product_recommended_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val itemData = differ.currentList[position]
        holder.bind(itemData)

        holder.itemView.setOnClickListener{
            onItemClickListener?.onClick(position,getItemId(position),itemData)
        }
    }
}