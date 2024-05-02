package com.mavalore.tricenari.adapter.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mavalore.tricenari.R
import com.mavalore.tricenari.domain.models.article.ArticleData

class ArticleHorizontalAdapter:RecyclerView.Adapter<ArticleHorizontalAdapter.HorizontalViewHolder>() {

    inner class HorizontalViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        private val articleImage: ImageView = itemView.findViewById(R.id.ivPopularArticle)

        fun bind(articleData: ArticleData){
            Glide.with(itemView.context)
                .load("https://www.tricenari.com/writeups/images/${articleData.id}.png")
                .into(articleImage)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, dataItem:Long, articleData: ArticleData, nextArticle: ArticleData? )
    }

    private var onItemClickListener:OnClickListener? = null

    fun setOnItemClickListener(listener:OnClickListener){
        onItemClickListener = listener
    }

    //object for difference callback
    private val diffUtil = object : DiffUtil.ItemCallback<ArticleData>(){
        override fun areItemsTheSame(oldItem: ArticleData, newItem: ArticleData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticleData, newItem: ArticleData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        return HorizontalViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.single_popular_article_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val currentArticle = differ.currentList[position]
        holder.bind(currentArticle)

        // Check if next position is within bounds
        val nextPosition = position + 1
        val nextArticle: ArticleData? = if (nextPosition < differ.currentList.size) {
            differ.currentList[nextPosition]
        } else {
            null
        }
        holder.itemView.setOnClickListener{
            onItemClickListener?.onClick(position,getItemId(position),currentArticle,nextArticle)
        }
    }
}