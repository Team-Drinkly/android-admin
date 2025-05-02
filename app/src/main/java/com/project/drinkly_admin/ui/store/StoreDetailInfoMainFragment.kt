package com.project.drinkly_admin.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.request.store.StoreDetailRequest
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.databinding.FragmentStoreDetailInfoMainBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.StoreViewModel

class StoreDetailInfoMainFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailInfoMainBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    private var getStoreDetailInfo: StoreDetailResponse? = null

    private var isSaveInfo = MutableList(5) { false }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailInfoMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        MyApplication.storeId = arguments?.getInt("storeId") ?: 0
        viewModel.getStoreDetail(mainActivity, arguments?.getInt("storeId") ?: 0)

        observeViewModel()

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

            buttonNext.setOnClickListener {
                var storeInfo =
                    StoreDetailRequest(
                        isReady = true
                    )

                viewModel.editStoreInfo(mainActivity, MyApplication.storeId, storeInfo)
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
                isSaveInfo[0] = true
                buttonStoreInfo.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext1.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                isSaveInfo[0] = false
                buttonStoreInfo.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext1.setImageResource(R.drawable.ic_next)
            }
            // 제공하는 주류
            if(storeInfo?.availableDrinkImageUrls?.size != 0) {
                isSaveInfo[1] = true
                buttonAvailableDrink.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext2.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                isSaveInfo[1] = false
                buttonAvailableDrink.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext2.setImageResource(R.drawable.ic_next)
            }
            // 메뉴판
            if(storeInfo?.menuImageUrls?.size != 0) {
                isSaveInfo[2] = true
                buttonMenu.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext3.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                isSaveInfo[2] = false
                buttonMenu.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext3.setImageResource(R.drawable.ic_next)
            }
            // 영업시간
            if(storeInfo?.openingHours != null) {
                isSaveInfo[3] = true
                buttonOpenTime.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext4.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                isSaveInfo[3] = false
                buttonOpenTime.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext4.setImageResource(R.drawable.ic_next)
            }
            // 멤버십 이용 가능 요일
            if(storeInfo?.availableDays != null) {
                isSaveInfo[4] = true
                buttonAvailableDate.setBackgroundResource(R.drawable.background_primary10_radius10)
                imageViewInfoNext5.setImageResource(R.drawable.ic_check_circle_checked)
            } else {
                isSaveInfo[4] = false
                buttonAvailableDate.setBackgroundResource(R.drawable.background_white_radius10)
                imageViewInfoNext5.setImageResource(R.drawable.ic_next)
            }
        }
    }

    fun initView() {
        binding.run {

            if(isSaveInfo.all { it }) {
                buttonNext.visibility = View.VISIBLE
            } else {
                buttonNext.visibility = View.GONE
            }

            toolbar.run {
                textViewTitle.text = "매장 세부 정보 등록"
                buttonBack.setOnClickListener {
                    viewModel.storeDetailInfo.value = null
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

}