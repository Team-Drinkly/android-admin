package com.project.drinkly_admin.ui.coupon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentCouponCompleteBinding
import com.project.drinkly_admin.databinding.FragmentCouponCreateBinding
import com.project.drinkly_admin.ui.MainActivity

class CouponCompleteFragment : Fragment() {

    lateinit var binding: FragmentCouponCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCouponCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNext.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        return binding.root
    }


}