package com.project.drinkly_admin.ui.home.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.databinding.FragmentStoreDetailInfoMainBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.UserViewModel

class StoreDetailInfoMainFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailInfoMainBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    private var getStoreDetailInfo: StoreDetailResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailInfoMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

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
        viewModel.run {
            storeDetailInfo.observe(viewLifecycleOwner) {
                checkInfo(it)
            }
        }
    }

    fun checkInfo(storeInfo: StoreDetailResponse?) {
        binding.run {
            // 매장 정보
            if(storeInfo?.storeDescription != null) {
                buttonStoreInfo.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext1.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                buttonStoreInfo.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext1.setImageResource(R.drawable.ic_next)
            }
            // 제공하는 주류
            if(storeInfo?.availableDrinkImageUrls?.size != 0) {
                buttonAvailableDrink.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext2.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                buttonAvailableDrink.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext2.setImageResource(R.drawable.ic_next)
            }
            // 메뉴판
            if(storeInfo?.menuImageUrls?.size != 0) {
                buttonMenu.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext3.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                buttonMenu.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext3.setImageResource(R.drawable.ic_next)
            }
            // 영업시간
            if(storeInfo?.openingHours != null) {
                buttonOpenTime.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext4.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                buttonOpenTime.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext4.setImageResource(R.drawable.ic_next)
            }
            // 멤버십 이용 가능 요일
            if(storeInfo?.availableDays != null) {
                buttonAvailableDate.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext5.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                buttonAvailableDate.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext5.setImageResource(R.drawable.ic_next)
            }
        }
    }

    fun initView() {
        viewModel.getStoreDetail(mainActivity, arguments?.getInt("storeId") ?: 0)

        binding.run {
            toolbar.run {
                textViewTitle.text = "매장 세부 정보 등록"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}