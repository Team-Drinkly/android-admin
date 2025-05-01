package com.project.drinkly_admin.ui.coupon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.response.coupon.CouponListResponse
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.databinding.FragmentCouponBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.coupon.adapter.CouponAdapter
import com.project.drinkly_admin.ui.home.adapter.OrderHistoryAdapter
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.CouponViewModel
import com.project.drinkly_admin.viewModel.OrderViewModel

class CouponFragment : Fragment() {

    lateinit var binding: FragmentCouponBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: CouponViewModel by lazy {
        ViewModelProvider(requireActivity())[CouponViewModel::class.java]
    }

    var getCoupons: MutableList<CouponListResponse>? = mutableListOf()

    lateinit var couponAdapter: CouponAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCouponBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        couponAdapter =
            CouponAdapter(mainActivity, getCoupons)

        binding.run {
            recyclerViewOrderHistory.apply {
                adapter = couponAdapter
                layoutManager = LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            storeCoupons.observe(viewLifecycleOwner) {
                getCoupons = it as MutableList<CouponListResponse>?

                couponAdapter.updateList(getCoupons)
            }
        }
    }

    fun initView() {
        viewModel.getCouponList(mainActivity, MyApplication.storeId)

        binding.run {
            toolbar.run {
                textViewTitle.text = "쿠폰 관리"
                buttonBack.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

}