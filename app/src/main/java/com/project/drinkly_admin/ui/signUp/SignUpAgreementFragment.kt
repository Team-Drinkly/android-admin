package com.project.drinkly_admin.ui.signUp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentSignUpAgreementBinding
import com.project.drinkly_admin.ui.MainActivity

class SignUpAgreementFragment : Fragment() {

    lateinit var binding: FragmentSignUpAgreementBinding
    lateinit var mainActivity: MainActivity

    val isAgree = MutableList(3) { false }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpAgreementBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutAgreementAll.setOnClickListener {
                isAgree[0] = !isAgree[0]
                checkAgreementAll()
            }
            checkbox1.setOnClickListener {
                isAgree[1] = !isAgree[1]
                checkAgree(1, checkbox1)
            }
            checkbox2.setOnClickListener {
                isAgree[2] = !isAgree[2]
                checkAgree(2, checkbox2)
            }

            // 운영정책 및 이용약관
            textViewAgreement1.setOnClickListener {
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drinkly.notion.site/1de09cf02bf9802ca5cef1af2451833d"))
                startActivity(intent)
            }

            // 개인정보 수집 및 이용동의서 확인
            textViewAgreement2.setOnClickListener {
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drinkly.notion.site/1de09cf02bf98035afefe350bbbcc716"))
                startActivity(intent)
            }

            buttonNext.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SignUpPassFragment())
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

    fun checkAgreementAll() {
        binding.run {
            if(isAgree[0]) {
                checkboxAll.setImageResource(R.drawable.ic_checkbox_checked)
                isAgree.fill(true)
                checkAgreementAllWithOthers()
            } else {
                checkboxAll.setImageResource(R.drawable.ic_checkbox_unchecked)
                isAgree.fill(false)
                checkAgreementAllWithOthers()
            }
        }
        checkEnable()
    }

    fun checkAgreementAllWithOthers() {
        binding.run {
            listOf(
                checkbox1,
                checkbox2
            ).forEachIndexed { index, checkbox ->
                checkAgree(index + 1, checkbox)
            }
        }
    }

    fun checkAgree(position: Int, view: ImageView) {
        if(isAgree[position]) {
            view.setImageResource(R.drawable.ic_check_checked)
        } else {
            view.setImageResource(R.drawable.ic_check_unchecked)
        }
        checkEnable()
        if(isAgree[1] && isAgree[2]) {
            isAgree[0] = true
            binding.checkboxAll.setImageResource(R.drawable.ic_checkbox_checked)
        } else {
            isAgree[0] = false
            binding.checkboxAll.setImageResource(R.drawable.ic_checkbox_unchecked)
        }
    }

    fun checkEnable() {
        binding.run {
            if(isAgree[1] && isAgree[2]) {
                buttonNext.isEnabled = true
            } else {
                buttonNext.isEnabled = false
            }
        }
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "약관 동의"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}