package com.project.drinkly_admin.util

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MainUtil {
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    // 투명한 status bar
    fun Activity.setStatusBarTransparent() {
        // 상태바를 투명하게 설정하고, 레이아웃을 상태바까지 확장
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        if (Build.VERSION.SDK_INT >= 30) {  // API 30 이상
//            WindowCompat.setDecorFitsSystemWindows(window, false)

            // 레이아웃이 상태바를 침범하되, 네비게이션 바는 침범하지 않도록 설정
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                val systemBarsInsets = insets.getInsets(WindowInsets.Type.systemBars())
                val statusBarHeight = systemBarsInsets.top
                val navigationBarHeight = systemBarsInsets.bottom

                // 상태바 위로부터 시작 (상단 패딩 설정)
                view.setPadding(0, statusBarHeight, 0, 0)

                // 네비게이션 바를 침범하지 않도록 하단 패딩 설정
                view.setPadding(0, 0, 0, navigationBarHeight)

                insets
            }
        } else {
            // API 29 이하: 상태바는 침범, 네비게이션 바는 그대로
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  // 상태바를 침범
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 네비게이션 바는 그대로
                    )
        }
    }

    fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    fun updateViewPositionForKeyboard(targetView: View, keyboardHeight: Int) {
        val layoutParams = targetView.layoutParams
        if (layoutParams is ConstraintLayout.LayoutParams) {
            if (keyboardHeight > 0) {
                layoutParams.bottomMargin = keyboardHeight
            } else {
                layoutParams.bottomMargin = 0
            }
            targetView.layoutParams = layoutParams
        }
    }

    // editText 시간 단위 변환
    fun formatToTime(input: String): String {
        val digits = input.filter { it.isDigit() }.take(4) // 최대 4자리까지만 허용 (HHmm)

        return when (digits.length) {
            0 -> ""
            1, 2 -> digits
            3 -> "${digits.substring(0, 2)}:${digits.substring(2)}"
            4 -> "${digits.substring(0, 2)}:${digits.substring(2)}"
            else -> digits
        }
    }

    // 현재 시간 단위 변환
    fun getCurrentTimeFormatted(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26 이상
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            now.format(formatter)
        } else {
            // API 26 미만
            val now = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            formatter.format(now)
        }
    }

    // 오늘 날짜 형식 변환
    fun getTodayDateString(): String {
        val calendar = Calendar.getInstance() // 현재 날짜 가져오기
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        return dateFormat.format(calendar.time) // 날짜를 원하는 형식으로 변환
    }

    // 시간 단위 유효성 확인
    fun isValidTimeFormat(time: String?): Boolean {
        if (time == null || !time.matches(Regex("\\d{2}:\\d{2}"))) return false

        val (hour, minute) = time.split(":").map { it.toIntOrNull() ?: return false }

        return hour in 0..23 && minute in 0..59
    }
}