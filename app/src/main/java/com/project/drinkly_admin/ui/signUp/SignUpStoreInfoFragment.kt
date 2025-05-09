package com.project.drinkly_admin.ui.signUp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentSignUpStoreInfoBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MainUtil.updateViewPositionForKeyboard
import com.project.drinkly_admin.viewModel.LoginViewModel

class SignUpStoreInfoFragment : Fragment() {

    lateinit var binding: FragmentSignUpStoreInfoBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private val kakaoAddressLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val address = result.data?.getStringExtra("address")
            binding.editTextStoreAddressMain.run {
                setText(address)
                setBackgroundResource(R.drawable.background_edittext_radius50_filled)
            }
            checkEnabled()
        } else {
            println("error")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpStoreInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView.rootView) { _, insets ->
            val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            updateViewPositionForKeyboard(binding.scrollView, imeHeight - sysBarInsets.bottom)
            insets
        }

        binding.run {
            scrollView.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()
                false
            }

            editTextStoreName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    checkInput()
                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editTextStoreAddressMain.run {
                setOnClickListener {
                    // 도로명 주소 검색 기능
                    openKakaoAddressWebView()
                }

                addTextChangedListener {
                    checkInput()
                }
            }

            editTextStoreAddressDetail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    checkInput()
                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            buttonNext.setOnClickListener {
                MyApplication.basicStoreInfo.storeName = editTextStoreName.text.toString()
                MyApplication.basicStoreInfo.storeAddress = editTextStoreAddressMain.text.toString()
                MyApplication.basicStoreInfo.storeDetailAddress = editTextStoreAddressDetail.text.toString()

                val bundle = Bundle().apply {
                    putBoolean("isAdd", arguments?.getBoolean("isAdd") ?: false)
                }

                // 전달할 Fragment 생성
                val  nextFragment = SignUpStoreNumberFragment().apply {
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

    private fun openKakaoAddressWebView() {
        val intent = Intent(mainActivity, KakaoAddressWebActivity::class.java)
        kakaoAddressLauncher.launch(intent)
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

    fun checkInput() {
        binding.run {
            if(editTextStoreName.text.isNotEmpty()) {
                editTextStoreName.setBackgroundResource(R.drawable.background_edittext_radius50_filled)
            } else {
                editTextStoreName.setBackgroundResource(R.drawable.background_edittext_radius50_default)
            }

            if(editTextStoreAddressMain.text.isNotEmpty()) {
                editTextStoreAddressMain.setBackgroundResource(R.drawable.background_edittext_radius50_filled)
            } else {
                editTextStoreAddressMain.setBackgroundResource(R.drawable.background_edittext_radius50_default)
            }

            if(editTextStoreAddressDetail.text.isNotEmpty()) {
                editTextStoreAddressDetail.setBackgroundResource(R.drawable.background_edittext_radius50_filled)
            } else {
                editTextStoreAddressDetail.setBackgroundResource(R.drawable.background_edittext_radius50_default)
            }
        }
    }

    fun initView() {
        viewModel.validBusinessInfo.value = null

        binding.run {
            checkInput()

            toolbar.run {
                textViewTitle.text = "매장 정보 입력"
                buttonBack.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }
}