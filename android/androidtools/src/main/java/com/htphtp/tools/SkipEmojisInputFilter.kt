package com.htphtp.tools

import android.text.InputFilter
import android.text.Spanned

/**
 * Created by htp on 2018/6/22.
 */
class SkipEmojisInputFilter : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        for (i in start until end) {
            val type = Character.getType(source[i])
            if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                return ""
            }
        }
        return null
    }
}