package com.project.drinkly_admin.ui.coupon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.api.response.coupon.CouponListResponse
import com.project.drinkly_admin.databinding.RowCouponBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.R

class CouponAdapter(
    private var activity: MainActivity,
    private var coupons: List<CouponListResponse>?
) :
    RecyclerView.Adapter<CouponAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newCoupons: List<CouponListResponse>?) {
        coupons = newCoupons
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = coupons?.get(position)

        with(holder.binding) {
            textViewCouponTitle.text = item?.title
            textViewCouponDescription.text = item?.description
            textViewCouponExpiredDate.text = "유효 기간 : ${item?.expirationDate}까지"
            textViewCouponNum.text = "${(item?.remainingCount ?: 0)} / ${item?.totalCount}개"
            if(item?.available == true) {
                imageViewCoupon.setImageResource(R.drawable.ic_coupon_primary50)
                textViewCouponNumTitle.text = "현재 남은 수량"
                layoutCoupon.setBackgroundResource(R.drawable.background_primary10_radius10)
                root.setBackgroundResource(R.drawable.background_white_radius10_stroke_gray3)
            } else {
                imageViewCoupon.setImageResource(R.drawable.ic_coupon_gray8)
                textViewCouponNumTitle.text = "총 발행 수량"
                layoutCoupon.setBackgroundResource(R.drawable.background_gray2_radius10)
                root.setBackgroundResource(R.drawable.background_gray3_radius10)
            }
        }
    }

    override fun getItemCount() = (coupons?.size ?: 0)

    inner class ViewHolder(val binding: RowCouponBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)
            }
        }
    }
}