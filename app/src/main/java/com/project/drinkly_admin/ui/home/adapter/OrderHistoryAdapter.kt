package com.project.drinkly_admin.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.api.response.home.OrderHistoryResponse
import com.project.drinkly_admin.api.response.home.StoreListResponse
import com.project.drinkly_admin.databinding.RowOrderHistoryBinding
import com.project.drinkly_admin.databinding.RowStoreListBinding
import com.project.drinkly_admin.ui.MainActivity

class OrderHistoryAdapter(
    private var activity: MainActivity,
    private var orderInfos: List<FreeDrinkHistory>?
) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newOrders: List<FreeDrinkHistory>?) {
        orderInfos = newOrders
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = orderInfos?.get(position)

        with(holder.binding) {
            textViewDrinks.text = item?.providedDrink
            textViewCustomerName.text = "사용 고객 : ${item?.memberNickname}"
            textViewOrderTime.text = item?.createdAt
        }
    }

    override fun getItemCount() = (orderInfos?.size ?: 0)

    inner class ViewHolder(val binding: RowOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}