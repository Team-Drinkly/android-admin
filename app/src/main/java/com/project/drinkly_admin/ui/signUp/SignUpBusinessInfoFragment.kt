package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentSignUpBusinessInfoBinding
import com.project.drinkly_admin.ui.MainActivity

class SignUpBusinessInfoFragment : Fragment() {

    lateinit var binding: FragmentSignUpBusinessInfoBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBusinessInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            editTextBusinessNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if(editTextBusinessNumber.text.isNotEmpty()) {
                        editTextBusinessNumber.setBackgroundResource(R.drawable.background_edittext_success)
                    } else {
                        editTextBusinessNumber.setBackgroundResource(R.drawable.background_edittext_default)
                    }

                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editTextOpenDate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if(editTextOpenDate.text.isNotEmpty()) {
                        editTextOpenDate.setBackgroundResource(R.drawable.background_edittext_success)
                    } else {
                        editTextOpenDate.setBackgroundResource(R.drawable.background_edittext_default)
                    }

                    checkEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun checkEnabled() {
        binding.run {
            if(editTextBusinessNumber.text.isNotEmpty() && editTextOpenDate.text.length == 8) {
                buttonNext.isEnabled = true
            } else {
                buttonNext.isEnabled = false
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