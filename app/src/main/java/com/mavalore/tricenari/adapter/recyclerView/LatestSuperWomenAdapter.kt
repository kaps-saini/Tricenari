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

class LatestSuperWomenAdapter:RecyclerView.Adapter<LatestSuperWomenAdapter.SuperWomenAdapter>(){

    inner class SuperWomenAdapter(itemView:View):RecyclerView.ViewHolder(itemView){
        private val name:TextView = itemView.findViewById(R.id.tvNameSWVerticle)
        private val image:ImageView = itemView.findViewById(R.id.ivSWVerticle)
        private val role:TextView = itemView.findViewById(R.id.tvRoleSWV)

         fun bind(superWomenData: SuperWomenData){
            name.text = superWomenData.name
            role.text = superWomenData.role
            Glide.with(itemView.context)
                .load("https://www.tricenari.com/superwomenPages/pics/${superWomenData.id}.jpg")
                .into(image)
        }
    }

    private val diffUtil = object: DiffUtil.ItemCallback<SuperWomenData>(){
        override fun areItemsTheSame(oldItem: SuperWomenData, newItem: SuperWomenData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SuperWomenData, newItem: SuperWomenData): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    interface OnClickListener{
        fun onClick(position: Int, dataItem:Long, articleData: SuperWomenData, nextArticle: SuperWomenData? )
    }

    private var onItemClickListener:OnClickListener? = null

    fun setOnItemClickListener(listener:OnClickListener){
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperWomenAdapter {
        return SuperWomenAdapter(LayoutInflater.from(parent.context).inflate(
            R.layout.single_super_women_vertical_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SuperWomenAdapter, position: Int) {
        val currentData = differ.currentList[position]
        holder.bind(currentData)

        // Check if next position is within bounds
        val nextPosition = position + 1
        val nextSuperWomenData: SuperWomenData? = if (nextPosition < differ.currentList.size) {
            differ.currentList[nextPosition]
        } else {
            null
        }
        holder.itemView.setOnClickListener{
            onItemClickListener?.onClick(position,getItemId(position),currentData,nextSuperWomenData)
        }

    }
}