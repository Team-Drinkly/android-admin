package com.project.drinkly_admin.ui.home.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentStoreDetailInfoMainBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MyApplication

class StoreDetailInfoMainFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailInfoMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailInfoMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonStoreInfo.setOnClickListener {
                // 매장 정보 등록 화면으로 이동
            }

            buttonAvailableDrink.setOnClickListener {
                // 제공하는 주류 등록 화면으로 이동
            }

            buttonMenu.setOnClickListener {
                // 메뉴판 등록 화면으로 이동
            }

            buttonOpenTime.setOnClickListener {
                // 영업시간 설정 화면으로 이동
            }

            buttonAvailableDate.setOnClickListener {
                // 멤버십 이용 가능 요일 설정 화면으로 이동
            }

            buttonNext.setOnClickListener {

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {

    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "매장 세부 정보 입력"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}