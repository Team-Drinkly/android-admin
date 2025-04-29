package com.project.drinkly_admin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentHomeBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.info.StoreAvailableDaysFragment
import com.project.drinkly_admin.ui.home.info.StoreDetailInfoMainFragment
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.UserViewModel

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonOrderHistory.setOnClickListener {
                // 주문 내역 관리 화면으로 이동
            }

            buttonCoupon.setOnClickListener {
                // 쿠폰 관리 화면으로 이동
            }

            buttonStoreInfo.setOnClickListener {
                // 정보 설정 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreDetailInfoMainFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonRefresh.setOnClickListener {

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
           textViewTitle.text = MyApplication.storeName
            textViewDescription.text = "${viewModel.userName} 사장님, 안녕하세요!"
        }
    }

}