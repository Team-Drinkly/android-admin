package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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
            textViewStoreAddressValue.text = "${MyApplication.basicStoreInfo.storeAddress}, ${MyApplication.basicStoreInfo.storeDetailAddress}"

            toolbar.run {
                textViewTitle.text = "입점 신청"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}