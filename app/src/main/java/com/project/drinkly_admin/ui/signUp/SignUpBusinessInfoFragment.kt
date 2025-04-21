package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentSignUpBusinessInfoBinding
import com.project.drinkly_admin.ui.BasicDialogInterface
import com.project.drinkly_admin.ui.DialogBasic
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.viewModel.LoginViewModel

class SignUpBusinessInfoFragment : Fragment() {

    lateinit var binding: FragmentSignUpBusinessInfoBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private var businessNumber = ""
    private var openDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBusinessInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        binding.run {
            editTextBusinessNumber.addTextChangedListener(object : TextWatcher {
                private var isFormatting: Boolean = false  // 무한 루프 방지용

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting) return

                    val digits = s.toString().filter { it.isDigit() }
                    val maxLength = 10
                    val limited = digits.take(maxLength)

                    val formatted = buildString {
                        for ((i, c) in limited.withIndex()) {
                            append(c)
                            if (i == 2 || i == 4) append("-")
                        }
                    }

                    isFormatting = true
                    editTextBusinessNumber.setText(formatted)
                    editTextBusinessNumber.setSelection(formatted.length)  // 커서 끝으로 이동
                    isFormatting = false

                    // 배경 설정
                    if (formatted.isNotEmpty()) {
                        editTextBusinessNumber.setBackgroundResource(R.drawable.background_edittext_success)
                    } else {
                        editTextBusinessNumber.setBackgroundResource(R.drawable.background_edittext_default)
                    }

                    businessNumber = limited

                    checkEnabled()
                }
            })


            editTextOpenDate.addTextChangedListener(object : TextWatcher {
                private var isFormatting: Boolean = false  // 무한 루프 방지용

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (isFormatting) return

                    val digits = s.toString().filter { it.isDigit() }
                    val limited = digits.take(8)  // yyyyMMdd 최대 8자리까지만 허용

                    val formatted = buildString {
                        for ((i, c) in limited.withIndex()) {
                            append(c)
                            if (i == 3 || i == 5) append("-")  // yyyy-MM-dd 형식
                        }
                    }

                    isFormatting = true
                    editTextOpenDate.setText(formatted)
                    editTextOpenDate.setSelection(formatted.length)
                    isFormatting = false

                    // 배경 설정
                    if (formatted.isNotEmpty()) {
                        editTextOpenDate.setBackgroundResource(R.drawable.background_edittext_success)
                    } else {
                        editTextOpenDate.setBackgroundResource(R.drawable.background_edittext_default)
                    }

                    // 하이픈 없는 원본 날짜 저장
                    openDate = limited

                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            buttonNext.setOnClickListener {
                MyApplication.basicStoreInfo.businessRegistrationNumber = editTextBusinessNumber.text.toString()
                viewModel.getOwnerName(mainActivity, businessNumber, openDate)
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
            if(businessNumber.length == 10 && openDate.length == 8) {
                buttonNext.isEnabled = true
            } else {
                buttonNext.isEnabled = false
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            validBusinessInfo.observe(viewLifecycleOwner) {
                when(it) {
                    "01" -> {
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, SignUpStoreInfoFragment())
                            .commit()
                    }
                    "02" -> {
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
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "사업자 정보 입력"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}