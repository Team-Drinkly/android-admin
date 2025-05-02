package com.project.drinkly_admin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.api.response.home.StoreListResponse
import com.project.drinkly_admin.databinding.FragmentHomeStoreListBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.adapter.StoreAdapter
import com.project.drinkly_admin.ui.store.StoreDetailInfoMainFragment
import com.project.drinkly_admin.ui.signUp.SignUpBusinessInfoFragment
import com.project.drinkly_admin.util.MyApplication
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

    lateinit var storeAdapter : StoreAdapter

    private var getStoreInfo: List<StoreListResponse>? = null
    private var getStoreDetailInfo: StoreDetailResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeStoreListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

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

        viewModel.getOwnerName(mainActivity)
        viewModel.getStoreList(mainActivity)

        binding.run {
            textViewTitle.text = "사장님, 안녕하세요!"
        }
    }

}