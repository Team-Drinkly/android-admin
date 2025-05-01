package com.project.drinkly_admin.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentStoreInfoEditBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.StoreViewModel

class StoreInfoEditFragment : Fragment() {

    lateinit var binding: FragmentStoreInfoEditBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreInfoEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel.getStoreDetail(mainActivity, MyApplication.storeId)

        binding.run {
            buttonStoreInfo.setOnClickListener {
                // 매장 정보 등록 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreDetailInfoFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonAvailableDrink.setOnClickListener {
                // 제공하는 주류 등록 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreDetailAvailableDrinksFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonMenu.setOnClickListener {
                // 메뉴판 등록 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreMenuFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonOpenTime.setOnClickListener {
                // 영업시간 설정 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreOpenTimeFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonAvailableDate.setOnClickListener {
                // 멤버십 이용 가능 요일 설정 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreAvailableDaysFragment())
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

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "매장 관리"
                buttonBack.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }
}