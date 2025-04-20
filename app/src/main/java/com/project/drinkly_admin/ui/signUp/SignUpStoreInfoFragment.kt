package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentSignUpStoreInfoBinding
import com.project.drinkly_admin.ui.MainActivity

class SignUpStoreInfoFragment : Fragment() {

    lateinit var binding: FragmentSignUpStoreInfoBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpStoreInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            editTextStoreName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if(editTextStoreName.text.isNotEmpty()) {
                        editTextStoreName.setBackgroundResource(R.drawable.background_edittext_success)
                    } else {
                        editTextStoreName.setBackgroundResource(R.drawable.background_edittext_default)
                    }

                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editTextStoreAddressMain.setOnClickListener {
                // 도로명 주소 검색 기능
            }

            editTextStoreAddressDetail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if(editTextStoreAddressDetail.text.isNotEmpty()) {
                        editTextStoreAddressDetail.setBackgroundResource(R.drawable.background_edittext_success)
                    } else {
                        editTextStoreAddressDetail.setBackgroundResource(R.drawable.background_edittext_default)
                    }

                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            buttonNext.setOnClickListener {
                MyApplication.basicStoreInfo.storeName = editTextStoreName.text.toString()
                MyApplication.basicStoreInfo.storeAddress = editTextStoreAddressMain.text.toString()
                MyApplication.basicStoreInfo.storeDetailAddress = editTextStoreAddressDetail.text.toString()

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SignUpStoreNumberFragment())
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun checkEnabled() {
        binding.run {
            if(editTextStoreName.text.isNotEmpty() && editTextStoreAddressMain.text.isNotEmpty()) {
                buttonNext.isEnabled = true
            } else {
                buttonNext.isEnabled = false
            }
        }
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "매장 정보 입력"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}