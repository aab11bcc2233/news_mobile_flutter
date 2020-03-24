package com.htphtp.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Created by htp on 2018/5/18.
 */
class ClipboardUtils {
    companion object {
        fun copy(context: Context, text: String) {
            val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            c.primaryClip = ClipData.newPlainText("", text)
        }
    }
}