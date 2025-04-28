package com.project.drinkly_admin.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly_admin.api.request.image.ImageData
import com.project.drinkly_admin.databinding.RowAvailableDrinkBinding
import com.project.drinkly_admin.ui.MainActivity
import java.io.File


class AvailableDrinkAdapter(
    private var activity: MainActivity,
    private var images: List<ImageData>?
) :
    RecyclerView.Adapter<AvailableDrinkAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newImages: List<ImageData>?) {
        images = newImages
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowAvailableDrinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       with(holder.binding) {
           textViewDrink.text = images?.get(position)?.description
           Glide.with(activity)
               .load(images?.get(position)?.image)
               .into(imageViewAvailableDrink)

       }
    }

    override fun getItemCount() = (images?.size ?: 0)

    inner class ViewHolder(val binding: RowAvailableDrinkBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDelete.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}