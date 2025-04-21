package com.project.drinkly_admin.ui.signUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentSignUpCompleteBinding
import com.project.drinkly_admin.ui.MainActivity

class SignUpCompleteFragment : Fragment() {

    lateinit var binding: FragmentSignUpCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }
}