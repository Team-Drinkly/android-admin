package com.project.drinkly_admin.ui.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.databinding.FragmentOrderHistoryBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.coupon.CouponCreateFragment
import com.project.drinkly_admin.ui.home.adapter.OrderHistoryAdapter
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.OrderViewModel

class OrderHistoryFragment : Fragment() {

    lateinit var binding: FragmentOrderHistoryBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: OrderViewModel by lazy {
        ViewModelProvider(requireActivity())[OrderViewModel::class.java]
    }


    var getOrderHistory: MutableList<FreeDrinkHistory>? = mutableListOf()

    lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrderHistoryBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            buttonCreateCoupon.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, CouponCreateFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        orderHistoryAdapter =
            OrderHistoryAdapter(mainActivity, getOrderHistory)

        binding.run {
            recyclerViewOrderHistory.apply {
                adapter = orderHistoryAdapter
                layoutManager = LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            storeHomeOrderHistory.observe(viewLifecycleOwner) {
                getOrderHistory = it?.getFreeDrinkHistoryResponseList as MutableList<FreeDrinkHistory>?

                binding.run {
                    textViewTitle.text = it?.storeName

                    if(getOrderHistory?.size == 0 || getOrderHistory?.isEmpty() == true) {
                        layoutEmpty.visibility = View.VISIBLE
                        recyclerViewOrderHistory.visibility = View.GONE
                    } else {
                        Log.d("##", "size : ${getOrderHistory?.size}")
                        layoutEmpty.visibility = View.GONE
                        recyclerViewOrderHistory.visibility = View.VISIBLE
                        orderHistoryAdapter.updateList(getOrderHistory)
                    }
                }
            }
        }
    }

    fun initView() {
        viewModel.getOrderHistory(mainActivity, MyApplication.storeId)

        binding.run {
            toolbar.run {
                textViewTitle.text = "주문 내역"
                buttonBack.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

}