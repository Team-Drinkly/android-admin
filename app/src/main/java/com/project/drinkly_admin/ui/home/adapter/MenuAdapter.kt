package com.project.drinkly_admin.ui.home.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.RowAvailableDrinkBinding
import com.project.drinkly_admin.databinding.RowMenuBinding
import com.project.drinkly_admin.ui.MainActivity
import java.io.File

class MenuAdapter(
    private var activity: MainActivity,
    private var images: List<Any>?
) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    var deleteClickListener: OnItemDeleteClickListener? = null
    var galleryClickListener: OnItemGalleryClickListener? = null
    private var context: Context? = null

    interface OnItemDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    interface OnItemGalleryClickListener {
        fun onGalleryClick(position: Int)
    }

    fun updateList(newImages: List<Any>?) {
        images = newImages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            if (position == 0) {
                buttonDelete.visibility = View.INVISIBLE
                imageViewGallery.visibility = View.VISIBLE
                imageViewMenu.setImageResource(R.drawable.background_gray3_radius20)
                imageViewMenu.setOnClickListener {
                    galleryClickListener?.onGalleryClick(position)
                }
            } else {
                println("position : ${position - 1}")
                buttonDelete.visibility = View.VISIBLE
                imageViewGallery.visibility = View.GONE

                // 이미지 로딩
                when (images?.get(position - 1)) {
                    is File -> {
                        val file = images?.get(position - 1) as File
                        Glide.with(activity)
                            .load(file)
                            .into(imageViewMenu)
                    }
                    is String -> {
                        println("string")
                        Glide.with(activity)
                            .load(images?.get(position - 1))
                            .into(imageViewMenu)
                    }
                    else -> {
                        println("else")
                        imageViewMenu.setImageResource(R.drawable.background_gray3_radius20)
                    }
                }
            }
        }
    }

    override fun getItemCount() = (images?.size ?: 0) + 1

    inner class ViewHolder(val binding: RowMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDelete.setOnClickListener {
                deleteClickListener?.onDeleteClick(adapterPosition)
            }
        }
    }
}