package com.project.drinkly_admin.ui.coupon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.FragmentCouponCreateBinding
import com.project.drinkly_admin.ui.BasicDialogInterface
import com.project.drinkly_admin.ui.DialogBasic
import com.project.drinkly_admin.ui.DialogSelect
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.SelectDialogInterface
import com.project.drinkly_admin.util.MainUtil.getTodayDateString
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.viewModel.CouponViewModel

class CouponCreateFragment : Fragment() {

    lateinit var binding: FragmentCouponCreateBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: CouponViewModel by lazy {
        ViewModelProvider(requireActivity())[CouponViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCouponCreateBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            editTextCouponTitle.addTextChangedListener {
                checkEnabled()
            }
            editTextCouponNum.addTextChangedListener {
                checkEnabled()
            }
            editTextCouponDate.setOnClickListener {
                val dateBottomsheet =
                    DateBottomSheetFragment(getTodayDateString())

                dateBottomsheet.setDateBottomSheetInterface(object : DateBottomSheetInterface {
                    override fun onDateClickCompleteButton(date: String) {
                        editTextCouponDate.setText(date)
                        checkEnabled()
                    }
                })

                dateBottomsheet.show(
                    mainActivity.supportFragmentManager,
                    "DateBottomsheet"
                )
            }

            buttonCreateCoupon.setOnClickListener {
                val dialog = DialogSelect("쿠폰을 발행하시겠어요?\n발행된 쿠폰은 수정 또는 삭제가 불가능해요")

                dialog.setSelectDialogInterface(object : SelectDialogInterface {
                    override fun onClickYesButton() {
                    }
                })

                dialog.show(mainActivity.supportFragmentManager, "DialogCreateCoupon")
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun checkEnabled() {
        binding.run {
            if(editTextCouponTitle.text.isNotEmpty() && editTextCouponNum.text.isNotEmpty() && editTextCouponDate.text.isNotEmpty()) {
                buttonCreateCoupon.isEnabled = true
            } else {
                buttonCreateCoupon.isEnabled = false
            }
        }
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "쿠폰 발행"
                buttonBack.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

}