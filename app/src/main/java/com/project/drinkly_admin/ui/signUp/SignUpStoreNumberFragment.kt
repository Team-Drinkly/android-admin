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
import com.project.drinkly_admin.databinding.FragmentSignUpStoreNumberBinding
import com.project.drinkly_admin.ui.MainActivity

class SignUpStoreNumberFragment : Fragment() {

    lateinit var binding: FragmentSignUpStoreNumberBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpStoreNumberBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            editTextStoreNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if(editTextStoreNumber.text.isNotEmpty()) {
                        editTextStoreNumber.setBackgroundResource(R.drawable.background_edittext_success)
                        buttonNext.isEnabled = true
                    } else {
                        editTextStoreNumber.setBackgroundResource(R.drawable.background_edittext_default)
                        buttonNext.isEnabled = false
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            buttonNext.setOnClickListener {
                MyApplication.basicStoreInfo.storeTel = editTextStoreNumber.text.toString()

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SignUpInfoFragment())
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