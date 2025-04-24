package com.project.drinkly_admin.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.api.response.home.StoreListResponse
import com.project.drinkly_admin.databinding.RowStoreListBinding
import com.project.drinkly_admin.ui.MainActivity

class StoreAdapter(
    private var activity: MainActivity,
    private var storeInfos: List<StoreListResponse>?
) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newStores: List<StoreListResponse>?) {
        storeInfos = newStores
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = storeInfos?.get(position)

        with(holder.binding) {
            textViewStoreName.text = item?.storeName
            textViewStoreAddress.text = item?.storeAddress
        }
    }

    override fun getItemCount() = (storeInfos?.size ?: 0)

    inner class ViewHolder(val binding: RowStoreListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}