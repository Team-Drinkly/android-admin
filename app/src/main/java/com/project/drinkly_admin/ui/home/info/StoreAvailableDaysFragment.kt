package com.project.drinkly_admin.ui.home.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentStoreAvailableDaysBinding
import com.project.drinkly_admin.databinding.LayoutStoreAvailableDaysBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.viewModel.StoreViewModel

class StoreAvailableDaysFragment : Fragment() {

    lateinit var binding: FragmentStoreAvailableDaysBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    private lateinit var dayLayoutMap: Map<String, LayoutStoreAvailableDaysBinding>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreAvailableDaysBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        dayLayoutMap = mapOf(
            "월" to binding.layoutStoreMonAvailableDays,
            "화" to binding.layoutStoreTueAvailableDays,
            "수" to binding.layoutStoreWedAvailableDays,
            "목" to binding.layoutStoreThuAvailableDays,
            "금" to binding.layoutStoreFriAvailableDays,
            "토" to binding.layoutStoreSatAvailableDays,
            "일" to binding.layoutStoreSunAvailableDays
        )

        setupSwitchListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun applyDefaultValue() {
        dayLayoutMap.forEach { (day, layout) ->
            layout.switchAvailable.isChecked = true
            layout.textViewDays.text = "${day}요일"
            layout.textViewAvailable.text = "멤버십 이용 가능"
        }
    }

    private fun applyAvailableDays() {
        val availableDaysString = viewModel.storeDetailInfo.value?.availableDays ?: return

        val availableDaysList = availableDaysString.split(" ")

        dayLayoutMap.forEach { (day, layout) ->
            layout.switchAvailable.isChecked = false
            layout.textViewDays.text = "${day}요일"
            layout.textViewAvailable.text = "멤버십 이용 불가능"
        }

        // availableDays에 포함된 요일만 switch 켜기
        availableDaysList.forEach { day ->
            val layout = dayLayoutMap[day]
            layout?.switchAvailable?.isChecked = true
            layout?.textViewAvailable?.text = "멤버십 이용 가능"
        }
    }

    private fun setupSwitchListeners() {
        dayLayoutMap.forEach { (_, layout) ->
            layout.switchAvailable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    layout.textViewAvailable.text = "멤버십 이용 가능"
                } else {
                    layout.textViewAvailable.text = "멤버십 이용 불가능"
                }
            }
        }
    }


    fun initView() {
        binding.run {
            val availableDays = viewModel.storeDetailInfo.value?.availableDays

            if(availableDays != null) {
                applyAvailableDays()
            } else {
                applyDefaultValue()
            }

            toolbar.run {
                textViewTitle.text = "멤버십 이용 가능 요일"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}