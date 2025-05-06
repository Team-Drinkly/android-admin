package com.project.drinkly_admin.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.request.store.OpeningHour
import com.project.drinkly_admin.api.response.home.StoreOpeningHour
import com.project.drinkly_admin.databinding.FragmentStoreOpenTimeBinding
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MainUtil
import com.project.drinkly_admin.util.MainUtil.updateViewPositionForKeyboard
import com.project.drinkly_admin.viewModel.StoreViewModel
import androidx.core.view.isVisible
import com.project.drinkly_admin.api.request.store.StoreDetailRequest
import com.project.drinkly_admin.ui.BasicDialogInterface
import com.project.drinkly_admin.ui.DialogBasic
import com.project.drinkly_admin.util.MainUtil.isValidTimeFormat
import com.project.drinkly_admin.util.MyApplication

class StoreOpenTimeFragment : Fragment() {

    lateinit var binding: FragmentStoreOpenTimeBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    var isFirstOpenTimeInputDone = false
    var isFirstCloseTimeInputDone = false

    private val dayIdMap by lazy {
        mapOf(
            "MONDAY" to binding.layoutStoreMonOpenTime,
            "TUESDAY" to binding.layoutStoreTueOpenTime,
            "WEDNESDAY" to binding.layoutStoreWedOpenTime,
            "THURSDAY" to binding.layoutStoreThuOpenTime,
            "FRIDAY" to binding.layoutStoreFriOpenTime,
            "SATURDAY" to binding.layoutStoreSatOpenTime,
            "SUNDAY" to binding.layoutStoreSunOpenTime
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreOpenTimeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView.rootView) { _, insets ->
            val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom + 100
            updateViewPositionForKeyboard(binding.scrollView, imeHeight - sysBarInsets.bottom)
            insets
        }

        checkButton()
        setupOpenTimeSync()

        binding.run {
            scrollView.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()
                false
            }

            buttonSave.setOnClickListener {
                var time = collectOpeningHours()

                if(time.isNotEmpty()) {
                    var storeInfo = StoreDetailRequest(
                        openingHours = collectOpeningHours()
                    )

                    viewModel.editStoreInfo(mainActivity, MyApplication.storeId, storeInfo)
                }
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
            applyDefaultValue()
            val openHours = viewModel.storeDetailInfo.value?.openingHours
            if(openHours != null) {
               applyOpeningHours(openHours)
            }

            toolbar.run {
                textViewTitle.text = "영업시간"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun applyDefaultValue() {
        binding.run {
            layoutStoreMonOpenTime.run {
                textViewDays.text = "월요일"
            }
            layoutStoreTueOpenTime.run {
                textViewDays.text = "화요일"
            }
            layoutStoreWedOpenTime.run {
                textViewDays.text = "수요일"
            }
            layoutStoreThuOpenTime.run {
                textViewDays.text = "목요일"
            }
            layoutStoreFriOpenTime.run {
                textViewDays.text = "금요일"
            }
            layoutStoreSatOpenTime.run {
                textViewDays.text = "토요일"
            }
            layoutStoreSunOpenTime.run {
                textViewDays.text = "일요일"
            }
        }
    }

    private fun applyOpeningHours(openingHours: List<StoreOpeningHour>) {
        for (dayInfo in openingHours) {
            val layout = dayIdMap[dayInfo.day] ?: continue

            layout.run {
                if (dayInfo.isOpen == true) {
                    checkbox.setImageResource(R.drawable.ic_checkbox_unchecked_gray7)
                    layoutTime.visibility = View.VISIBLE
                    editTextStoreOpenTime.setText(dayInfo.openTime ?: "")
                    editTextStoreCloseTime.setText(dayInfo.closeTime ?: "")
                } else {
                    checkbox.setImageResource(R.drawable.ic_checkbox_checked)
                    layoutTime.visibility = View.GONE
                }
            }
        }
    }

    private fun validateOpeningHours(): Boolean {
        dayIdMap.values.forEach { layout ->
            val isOpen = layout.layoutTime.visibility == View.VISIBLE
            val openTimeFilled = layout.editTextStoreOpenTime.text.length == 5
            val closeTimeFilled = layout.editTextStoreCloseTime.text.length == 5

            if (isOpen) {
                if (!openTimeFilled || !closeTimeFilled) {
                    return false
                }
            }
        }
        return true
    }

    private fun updateSaveButtonState() {
        binding.buttonSave.isEnabled = validateOpeningHours()
    }

    private fun collectOpeningHours(): List<OpeningHour> {
        val openingHours = mutableListOf<OpeningHour>()

        val dayKeyList = listOf(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        )

        dayKeyList.forEach { dayKey ->
            val layout = dayIdMap[dayKey] ?: return@forEach

            val isOpen = layout.layoutTime.isVisible
            val openTime = layout.editTextStoreOpenTime.text.toString().takeIf { it.isNotBlank() }
            val closeTime = layout.editTextStoreCloseTime.text.toString().takeIf { it.isNotBlank() }

            if (isOpen) {
                // 시간 단위 유효성 검사
                if (!isValidTimeFormat(openTime) || !isValidTimeFormat(closeTime)) {
                    val dialog = DialogBasic("영업 시작 및 종료 시간은 00:00부터\n23:59까지의 형식으로 입력해주세요!")

                    dialog.setBasicDialogInterface(object : BasicDialogInterface {
                        override fun onClickYesButton() {

                        }
                    })

                    dialog.show(mainActivity.supportFragmentManager, "DialogStoreOpenTime")

                    return emptyList()
                }
            }

            val openingHour = OpeningHour(
                day = dayKey,
                isOpen = isOpen,
                openTime = if (isOpen) openTime else null,
                closeTime = if (isOpen) closeTime else null
            )

            openingHours.add(openingHour)
        }

        return openingHours
    }

    private fun setupOpenTimeSync() {
        val openTimeEditTexts = listOf(
            binding.layoutStoreMonOpenTime.editTextStoreOpenTime,
            binding.layoutStoreTueOpenTime.editTextStoreOpenTime,
            binding.layoutStoreWedOpenTime.editTextStoreOpenTime,
            binding.layoutStoreThuOpenTime.editTextStoreOpenTime,
            binding.layoutStoreFriOpenTime.editTextStoreOpenTime,
            binding.layoutStoreSatOpenTime.editTextStoreOpenTime,
            binding.layoutStoreSunOpenTime.editTextStoreOpenTime
        )

        val closeTimeEditTexts = listOf(
            binding.layoutStoreMonOpenTime.editTextStoreCloseTime,
            binding.layoutStoreTueOpenTime.editTextStoreCloseTime,
            binding.layoutStoreWedOpenTime.editTextStoreCloseTime,
            binding.layoutStoreThuOpenTime.editTextStoreCloseTime,
            binding.layoutStoreFriOpenTime.editTextStoreCloseTime,
            binding.layoutStoreSatOpenTime.editTextStoreCloseTime,
            binding.layoutStoreSunOpenTime.editTextStoreCloseTime
        )

        openTimeEditTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener { editable ->
                val rawText = editable?.toString() ?: ""

                // 무조건 현재 입력은 포맷을 적용해서 보여줘야 함
                val formattedTime = MainUtil.formatToTime(rawText)

                if (editText.text.toString() != formattedTime) {
                    editText.setText(formattedTime)
                    editText.setSelection(formattedTime.length)
                }

                // 아직 첫 입력이 완료되지 않았다면 -> 다른 요일에도 복사
                if (!isFirstOpenTimeInputDone && formattedTime.isNotEmpty()) {
                    openTimeEditTexts.forEachIndexed { innerIndex, target ->
                        if (innerIndex != index) {
                            if (target.text.toString() != formattedTime) {
                                target.setText(formattedTime)
                                target.setSelection(formattedTime.length)
                            }
                        }
                    }

                    if (formattedTime.length == 5) {
                        isFirstOpenTimeInputDone = true
                    }
                }

                updateSaveButtonState()
            }
        }

        closeTimeEditTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener { editable ->
                val rawText = editable?.toString() ?: ""

                // 무조건 현재 입력은 포맷을 적용해서 보여줘야 함
                val formattedTime = MainUtil.formatToTime(rawText)

                if (editText.text.toString() != formattedTime) {
                    editText.setText(formattedTime)
                    editText.setSelection(formattedTime.length)
                }

                // 아직 첫 입력이 완료되지 않았다면 -> 다른 요일에도 복사
                if (!isFirstCloseTimeInputDone && formattedTime.isNotEmpty()) {
                    closeTimeEditTexts.forEachIndexed { innerIndex, target ->
                        if (innerIndex != index) {
                            if (target.text.toString() != formattedTime) {
                                target.setText(formattedTime)
                                target.setSelection(formattedTime.length)
                            }
                        }
                    }

                    if (formattedTime.length == 5) {
                        isFirstCloseTimeInputDone = true
                    }
                }

                updateSaveButtonState()
            }
        }
    }


    private fun checkButton() {
        binding.run {
            dayIdMap.values.forEach { layout ->

                layout.run {
                    checkbox.setOnClickListener {
                        val isVisible = layoutTime.visibility == View.VISIBLE
                        if (isVisible) {
                            layoutTime.visibility = View.GONE
                            checkbox.setImageResource(R.drawable.ic_checkbox_checked)
                            editTextStoreOpenTime.setText("")
                            editTextStoreCloseTime.setText("")
                        } else {
                            layoutTime.visibility = View.VISIBLE
                            checkbox.setImageResource(R.drawable.ic_checkbox_unchecked_gray7)
                            editTextStoreOpenTime.setText("")
                            editTextStoreCloseTime.setText("")
                        }

                        updateSaveButtonState()
                    }
                }

            }
        }
    }


}