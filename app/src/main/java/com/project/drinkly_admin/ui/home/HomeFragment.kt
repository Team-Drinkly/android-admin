package com.project.drinkly_admin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.databinding.FragmentHomeBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.coupon.CouponFragment
import com.project.drinkly_admin.ui.home.adapter.OrderHistoryAdapter
import com.project.drinkly_admin.ui.store.StoreDetailInfoMainFragment
import com.project.drinkly_admin.ui.order.OrderHistoryFragment
import com.project.drinkly_admin.ui.store.StoreInfoEditFragment
import com.project.drinkly_admin.util.MainUtil.getCurrentTimeFormatted
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.OrderViewModel
import com.project.drinkly_admin.viewModel.StoreViewModel
import com.project.drinkly_admin.viewModel.UserViewModel

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: OrderViewModel by lazy {
        ViewModelProvider(requireActivity())[OrderViewModel::class.java]
    }
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }
    private val storeViewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    var getOrderHistory: MutableList<FreeDrinkHistory>? = mutableListOf()

    lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }

        binding.run {
            buttonOrderHistory.setOnClickListener {
                // 주문 내역 관리 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, OrderHistoryFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonCoupon.setOnClickListener {
                // 쿠폰 관리 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, CouponFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonStoreInfo.setOnClickListener {
                // 정보 설정 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreInfoEditFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonChangeStore.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, HomeStoreListFragment())
                    .commit()
            }

            buttonRefresh.setOnClickListener {
                textViewRefreshTime.text = "${getCurrentTimeFormatted()} 기준"
                viewModel.getHomeOrderHistory(mainActivity, MyApplication.storeId)
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

                    if(getOrderHistory?.size == 0) {
                        layoutEmpty.visibility = View.VISIBLE
                        recyclerViewOrderHistory.visibility = View.GONE
                    } else {
                        layoutEmpty.visibility = View.GONE
                        recyclerViewOrderHistory.visibility = View.VISIBLE
                        orderHistoryAdapter.updateList(getOrderHistory)
                    }
                }
            }
        }
    }

    fun initView() {
        storeViewModel.storeDetailInfo.value = null

        viewModel.getHomeOrderHistory(mainActivity, MyApplication.storeId)

        binding.run {
            textViewTitle.text = MyApplication.storeName
            textViewDescription.text = "${userViewModel.userName?.value} 사장님, 안녕하세요!"

            textViewRefreshTime.text = "${getCurrentTimeFormatted()} 기준"
        }
    }
}