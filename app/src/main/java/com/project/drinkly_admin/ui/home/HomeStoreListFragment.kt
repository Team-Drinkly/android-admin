package com.project.drinkly_admin.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.project.drinkly_admin.R
import com.project.drinkly_admin.UpdateManager
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.api.response.home.StoreListResponse
import com.project.drinkly_admin.databinding.FragmentHomeStoreListBinding
import com.project.drinkly_admin.ui.BasicDialogInterface
import com.project.drinkly_admin.ui.DialogBasic
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.adapter.StoreAdapter
import com.project.drinkly_admin.ui.signUp.SignUpBusinessInfoFragment
import com.project.drinkly_admin.ui.store.StoreDetailInfoMainFragment
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.CouponViewModel
import com.project.drinkly_admin.viewModel.OrderViewModel
import com.project.drinkly_admin.viewModel.StoreViewModel
import com.project.drinkly_admin.viewModel.UserViewModel


class HomeStoreListFragment : Fragment() {

    lateinit var binding: FragmentHomeStoreListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }
    private val storeViewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }
    private val couponViewModel: CouponViewModel by lazy {
        ViewModelProvider(requireActivity())[CouponViewModel::class.java]
    }
    private val orderViewModel: OrderViewModel by lazy {
        ViewModelProvider(requireActivity())[OrderViewModel::class.java]
    }

    private lateinit var appUpdateManager: AppUpdateManager
    private val MY_REQUEST_CODE = 1001
    private var isDialogShown = false

    lateinit var storeAdapter : StoreAdapter

    private var getStoreInfo: List<StoreListResponse>? = null
    private var getStoreDetailInfo: StoreDetailResponse? = null

    private var backPressedOnce = false
    private val backPressHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeStoreListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        appUpdateManager = AppUpdateManagerFactory.create(requireActivity())

        initAdapter()
        observeViewModel()

        binding.run {
            // 매장 추가
            buttonAddStore.setOnClickListener {
                val bundle = Bundle().apply {
                    putBoolean("isAdd", true)
                }

                // 전달할 Fragment 생성
                val  nextFragment = SignUpBusinessInfoFragment().apply {
                    arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                }
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, nextFragment)
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
        storeAdapter = StoreAdapter(mainActivity, getStoreInfo).apply {
            itemClickListener = object : StoreAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    MyApplication.storeId = getStoreInfo?.get(position)?.storeId ?: 0
                    MyApplication.storeName = getStoreInfo?.get(position)?.storeName ?: ""

                    storeViewModel.getStoreDetail(mainActivity, getStoreInfo?.get(position)?.storeId ?: 0)
                }
            }
        }

        binding.run {
            recyclerViewStore.apply {
                adapter = storeAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            userName.observe(viewLifecycleOwner) {
                binding.run {
                    textViewTitle.text = "${it} 사장님, 안녕하세요!"
                }
            }

            storeList.observe(viewLifecycleOwner) {
                getStoreInfo = it

                storeAdapter.updateList(getStoreInfo)
            }
        }

        storeViewModel.run {
            storeDetailInfo.observe(viewLifecycleOwner) {
                getStoreDetailInfo = it

                checkInfo()
            }
        }
    }

    fun checkInfo() {
        if(getStoreDetailInfo?.isReady == true) {
            couponViewModel.storeCoupons.value = null
            orderViewModel.run {
                storeHomeOrderHistory.value = null
                storeOrderHistory.value = null
            }

            val bundle = Bundle().apply {
                putInt("storeId", MyApplication.storeId)
            }

            // 전달할 Fragment 생성
            val  nextFragment = HomeFragment().apply {
                arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
            }
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, nextFragment)
                .commit()
        } else if(getStoreDetailInfo?.isReady == false) {

            val bundle = Bundle().apply {
                putInt("storeId", MyApplication.storeId)
            }

            // 전달할 Fragment 생성
            val  nextFragment = StoreDetailInfoMainFragment().apply {
                arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
            }
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, nextFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun initView() {
        storeViewModel.storeDetailInfo.value = null
        getStoreDetailInfo = null

        checkForUpdate()

        viewModel.run {
            getOwnerName(mainActivity)
            getStoreList(mainActivity)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (backPressedOnce) {
                requireActivity().finishAffinity() // 앱 완전 종료
            } else {
                backPressedOnce = true
                Toast.makeText(requireContext(), "뒤로가기 버튼을\n한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()

                backPressHandler.postDelayed({
                    backPressedOnce = false
                }, 2000)
            }
        }
    }

    private fun checkForUpdate() {
        if (isDialogShown) return

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            if (isUpdateAvailable) {
                isDialogShown = true

                Log.e("##", "Update available")

                val dialog = DialogBasic("새로운 업데이트 사항이 있어요\n최신 버전으로 업데이트해주세요!", "업데이트 하기", false)

                dialog.setBasicDialogInterface(object : BasicDialogInterface {
                    override fun onClickYesButton() {
                        // IMMEDIATE 업데이트 요청
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            mainActivity,
                            MY_REQUEST_CODE
                        )
                    }
                })

                dialog.show(mainActivity.supportFragmentManager, "DialogUpdate")
            } else {
                Log.e("##", "No update available")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("AppUpdate", "업데이트 실패 또는 취소됨")

                checkForUpdate()
            }
        }
    }

}