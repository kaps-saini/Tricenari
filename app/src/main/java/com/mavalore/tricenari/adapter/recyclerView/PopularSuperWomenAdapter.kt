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
import com.mavalore.tricenari.domain.models.superwomen.SuperWomenData

class PopularSuperWomenAdapter:RecyclerView.Adapter<PopularSuperWomenAdapter.PopularViewHolder>() {

    inner class PopularViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val ivPopularSuperWomen:ImageView = itemView.findViewById(R.id.ivSWHorizontal)
        private val tvName:TextView = itemView.findViewById(R.id.tvSuperWomenName)

        fun bind(superWomenData: SuperWomenData){
                tvName.text = superWomenData.name
                Glide.with(itemView.context)
                    .load("https://www.tricenari.com/superwomenPages/pics/${superWomenData.id}.jpg")
                    .into(ivPopularSuperWomen)
        }
    }

    private val diffUtil = object:DiffUtil.ItemCallback<SuperWomenData>(){
        override fun areItemsTheSame(oldItem: SuperWomenData, newItem: SuperWomenData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SuperWomenData, newItem: SuperWomenData): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    interface OnClickListener{
        fun onClick(position: Int, dataItem:Long, currentWomen: SuperWomenData, nextWomen: SuperWomenData? )
    }

    private var onItemClickListener:OnClickListener? = null

    fun setOnItemClickListener(listener:OnClickListener){
        onItemClickListener = listener
    }

    fun submitList(superWomenDataList: List<SuperWomenData>) {
        val popularList = superWomenDataList.filter { it.popular == "1" }
        differ.submitList(popularList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.single_super_women_horizontal_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
       val currentData = differ.currentList[position]
        holder.bind(currentData)

        // Check if next position is within bounds
        val nextPosition = position + 1
        val nextData: SuperWomenData? = if (nextPosition < differ.currentList.size) {
            differ.currentList[nextPosition]
        } else {
            null
        }
        holder.itemView.setOnClickListener{
            onItemClickListener?.onClick(position,getItemId(position),currentData,nextData)
        }
    }
}