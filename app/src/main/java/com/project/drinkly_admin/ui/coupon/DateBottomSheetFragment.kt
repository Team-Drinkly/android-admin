package com.project.drinkly_admin.ui.coupon

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.project.drinkly_admin.databinding.FragmentDateBottomSheetBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface DateBottomSheetInterface {
    fun onDateClickCompleteButton(date: String)
}

class DateBottomSheetFragment(var disableDay: String) :
    DialogFragment() {

    lateinit var binding: FragmentDateBottomSheetBinding

    var date = ""

    // 인터페이스 인스턴스
    private var listener: DateBottomSheetInterface? = null

    // 리스너 설정 메서드
    fun setDateBottomSheetInterface(listener: DateBottomSheetInterface) {
        this.listener = listener
    }

    override fun onStart() {
        super.onStart()

        // 다이얼로그를 하단에 배치하고 마진 설정
        dialog?.window?.let { window ->
            val params = window.attributes

            // 화면 너비에서 좌우 마진과 하단 마진 계산
            val marginHorizontalInPx =
                (20 * requireContext().resources.displayMetrics.density).toInt() // 좌우 마진 20dp
            val marginBottomInPx =
                (11 * requireContext().resources.displayMetrics.density).toInt() // 하단 마진 11dp

            params.width =
                requireContext().resources.displayMetrics.widthPixels - (marginHorizontalInPx * 2) // 좌우 마진 적용
            params.height = WindowManager.LayoutParams.WRAP_CONTENT // 세로는 콘텐츠 크기
            params.gravity = android.view.Gravity.BOTTOM // 하단에 배치
            params.y = marginBottomInPx // 하단 마진 적용

            window.attributes = params
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDateBottomSheetBinding.inflate(inflater, container, false)

        // 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 클릭 이벤트 처리
        binding.run {

//            disableDatesBeforeTarget("2025년 03월 01일")

            val disabledDates = hashSetOf<CalendarDay>()

            binding.calendarRegisterDate.apply {
                // 휴무일 지정을 위한 Decorator 설정
                addDecorator(
                    DayDisableDecorator(
                        convertStringToCalendarDay(disableDay)
                    )
                )
                // 요일을 지정하귀 위해 {"월", "화", ..., "일"} 배열을 추가한다.
                setWeekDayLabels(arrayOf("월", "화", "수", "목", "금", "토", "일"))
                // 달력 상단에 `월 년` 포맷을 수정하기 위해 TitleFormatter 설정
                setTitleFormatter(MyTitleFormatter())
            }

            DateFormatTitleFormatter()

            buttonComplete.setOnClickListener {
                val date = binding.calendarRegisterDate.selectedDate?.let {
                    val calendar = Calendar.getInstance().apply {
                        set(it.year, it.month - 1, it.day) // month는 1부터 시작하는 CalendarDay 기준
                    }
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                    dateFormat.format(calendar.time)
                } ?: "날짜를 선택해주세요"
                listener?.onDateClickCompleteButton(date)
                dismiss()
            }
        }

        return binding.root
    }

    fun convertStringToCalendarDay(dateString: String): CalendarDay {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val date =
            dateFormat.parse(dateString) ?: return CalendarDay.today() // 변환 실패 시 오늘 날짜 반환

        val calendar = Calendar.getInstance().apply {
            time = date
        }

        return CalendarDay.from(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }


    inner class MyTitleFormatter : TitleFormatter {
        override fun format(day: CalendarDay?): CharSequence {
            day ?: return ""

            val calendar = Calendar.getInstance().apply {
                set(day.year, day.month - 1, day.day) // month가 0부터 시작하므로 -1 필요
            }

            val simpleDateFormat = SimpleDateFormat("yyyy년 MM월", Locale.KOREA)
            return simpleDateFormat.format(calendar.time)
        }
    }


    inner class DayDisableDecorator : DayViewDecorator {
        private var today: CalendarDay

        constructor(today: CalendarDay) {
            this.today = today
        }

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.isBefore(today)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.let { it.setDaysDisabled(true) }
        }
    }

}