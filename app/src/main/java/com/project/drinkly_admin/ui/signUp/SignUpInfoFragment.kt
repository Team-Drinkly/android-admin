package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.databinding.FragmentSignUpInfoBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.viewModel.LoginViewModel

class SignUpInfoFragment : Fragment() {

    lateinit var binding: FragmentSignUpInfoBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonSignUp.setOnClickListener {
                MyApplication.basicStoreInfo.ownerId = TokenManager(mainActivity).getUserId()

                if(arguments?.getBoolean("isAdd") == true) {
                    viewModel.saveBasicStoreInfo(mainActivity)
                } else {
                    viewModel.signUp(mainActivity)
                }
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
            textViewStoreNameValue.text = MyApplication.basicStoreInfo.storeName
            textViewStoreNumberValue.text = MyApplication.basicStoreInfo.storeTel

            if(MyApplication.basicStoreInfo.storeDetailAddress?.isNotEmpty() == true) {
                textViewStoreAddressValue.text = "${MyApplication.basicStoreInfo.storeAddress}, ${MyApplication.basicStoreInfo.storeDetailAddress}"
            } else {
                textViewStoreAddressValue.text = "${MyApplication.basicStoreInfo.storeAddress}"
            }

            toolbar.run {
                textViewTitle.text = "입점 신청"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}