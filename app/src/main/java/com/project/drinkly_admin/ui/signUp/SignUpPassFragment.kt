package com.project.drinkly_admin.ui.signUp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentSignUpPassBinding
import com.project.drinkly_admin.ui.BasicDialogInterface
import com.project.drinkly_admin.ui.DialogBasic
import com.project.drinkly_admin.ui.MainActivity

class SignUpPassFragment : Fragment() {

    lateinit var binding: FragmentSignUpPassBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpPassBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNext.setOnClickListener {
                // 패스 본인인증
                val passIntent = Intent(mainActivity, PassWebActivity::class.java)
                startActivity(passIntent)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
        checkPassResult()
    }

    fun checkPassResult() {
        if(MyApplication.signUpPassAuthorization == true) {
            // 패스 인증 완료 후 회원가입 - 사업자 정보 입력 화면 이동
            MyApplication.signUpPassAuthorization = null

            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, SignUpBusinessInfoFragment())
                .addToBackStack(null)
                .commit()
        } else if(MyApplication.signUpPassAuthorization == false) {
            MyApplication.signUpPassAuthorization = null

            val dialog = DialogBasic("이미 가입한 회원입니다.\n가입된 계정으로 로그인해주세요.")

            dialog.setBasicDialogInterface(object : BasicDialogInterface {
                override fun onClickYesButton() {
                    fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            })

            dialog.show(mainActivity.supportFragmentManager, "DialogPass")
        }
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "본인 인증"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}