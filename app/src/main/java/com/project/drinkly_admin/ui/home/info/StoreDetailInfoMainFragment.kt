package com.project.drinkly_admin.ui.home.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentStoreDetailInfoMainBinding
import com.project.drinkly_admin.ui.MainActivity

class StoreDetailInfoMainFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailInfoMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreDetailInfoMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

}