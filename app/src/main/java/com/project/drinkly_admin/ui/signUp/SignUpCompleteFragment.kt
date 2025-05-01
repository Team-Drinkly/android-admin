package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.request.login.BasicStoreInfoRequest
import com.project.drinkly_admin.databinding.FragmentSignUpCompleteBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.HomeStoreListFragment
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.LoginViewModel

class SignUpCompleteFragment : Fragment() {

    lateinit var binding: FragmentSignUpCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNext.setOnClickListener {
                // 값 초기화
                MyApplication.resetBasicStoreInfo()

                // 이전 BackStack의 모든 Fragment 제거
                mainActivity.supportFragmentManager.popBackStackImmediate(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, HomeStoreListFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }
}