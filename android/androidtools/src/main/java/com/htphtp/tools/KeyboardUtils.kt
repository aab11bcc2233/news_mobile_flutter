package com.htphtp.tools

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Created by xieshilei on 2018/3/27.
 */
class KeyboardUtils {
    companion object {
        fun showKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // 获取软键盘的显示状态
            val isOpen = imm.isActive()
            if (!isOpen) {
                // 强制显示软键盘
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
            }
        }
        fun closeKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // 获取软键盘的显示状态
            val isOpen = imm.isActive()
            if (isOpen) {
                // 强制隐藏软键盘
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }
    }




}