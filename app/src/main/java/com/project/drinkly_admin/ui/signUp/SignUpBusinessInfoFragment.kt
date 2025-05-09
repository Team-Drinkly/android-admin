package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.openData.request.ValidateBusinessInfoRequest
import com.project.drinkly_admin.api.request.login.BasicStoreInfoRequest
import com.project.drinkly_admin.databinding.FragmentSignUpBusinessInfoBinding
import com.project.drinkly_admin.ui.BasicDialogInterface
import com.project.drinkly_admin.ui.DialogBasic
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MainUtil.updateViewPositionForKeyboard
import com.project.drinkly_admin.viewModel.LoginViewModel
import com.project.drinkly_admin.viewModel.UserViewModel

class SignUpBusinessInfoFragment : Fragment() {

    lateinit var binding: FragmentSignUpBusinessInfoBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private var businessNumber = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBusinessInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView.rootView) { _, insets ->
            val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            updateViewPositionForKeyboard(binding.scrollView, imeHeight - sysBarInsets.bottom)
            insets
        }

        observeViewModel()

        binding.run {
            scrollView.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()
                false
            }

            editTextBusinessNumber.addTextChangedListener(object : TextWatcher {
                private var currentText = ""
                private var isFormatting = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    checkEnabled()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting) return

                    val digitsOnly = s.toString().filter { it.isDigit() }
                    val maxLength = 10
                    val limited = digitsOnly.take(maxLength)

                    val formatted = buildString {
                        for (i in limited.indices) {
                            append(limited[i])
                            if (i == 2 || i == 4) append("-")
                        }
                    }

                    if (formatted == currentText) return

                    isFormatting = true
                    currentText = formatted
                    val selectionIndex = formatted.length

                    editTextBusinessNumber.setText(formatted)
                    editTextBusinessNumber.setSelection(selectionIndex.coerceAtMost(formatted.length))
                    isFormatting = false

                    // 배경 설정
                    if (limited.isNotEmpty()) {
                        editTextBusinessNumber.setBackgroundResource(R.drawable.background_edittext_radius50_filled)
                    } else {
                        editTextBusinessNumber.setBackgroundResource(R.drawable.background_edittext_radius50_default)
                    }

                    businessNumber = limited // 하이픈 없는 숫자만 저장
                    checkEnabled()
                }
            })

            buttonNext.setOnClickListener {
                MyApplication.basicStoreInfo.businessRegistrationNumber = editTextBusinessNumber.text.toString()

                viewModel.validateBusinessInfo(mainActivity, ValidateBusinessInfoRequest(listOf(businessNumber)))
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
            if(businessNumber.length == 10) {
                buttonNext.isEnabled = true
            } else {
                buttonNext.isEnabled = false
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            validBusinessInfo.observe(viewLifecycleOwner) {
                if(it == true) {
                    val bundle = Bundle().apply {
                        putBoolean("isAdd", arguments?.getBoolean("isAdd") ?: false)
                    }

                    // 전달할 Fragment 생성
                    val  nextFragment = SignUpStoreInfoFragment().apply {
                        arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                    }
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .addToBackStack(null)
                        .commit()
                } else if(it == false) {
                    val dialog = DialogBasic("사업자 정보가 일치하지 않습니다.\n다시 한번 확인해주세요.")

                    dialog.setBasicDialogInterface(object : BasicDialogInterface {
                        override fun onClickYesButton() {

                        }
                    })

                    dialog.show(mainActivity.supportFragmentManager, "DialogBusinessInfo")
                }
            }
        }
    }

    fun initView() {
        MyApplication.resetBasicStoreInfo()

        binding.run {
            toolbar.run {
                textViewTitle.text = "사업자 정보 입력"
                buttonBack.setOnClickListener {
                    viewModel.validBusinessInfo.value = null
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}