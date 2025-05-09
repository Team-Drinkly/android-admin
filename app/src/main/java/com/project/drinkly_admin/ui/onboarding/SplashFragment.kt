package com.project.drinkly_admin.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.databinding.FragmentSplashBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.HomeStoreListFragment

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        Handler().postDelayed({
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, LoginFragment())
                .commit()
        }, 3000)

        return binding.root
    }

}