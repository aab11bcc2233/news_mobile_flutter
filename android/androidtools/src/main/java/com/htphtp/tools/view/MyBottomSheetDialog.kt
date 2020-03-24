package com.htphtp.tools.view

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by htp on 2018/9/3.
 */
class MyBottomSheetDialog : BottomSheetDialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, theme: Int) : super(context, theme)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, Resources.getSystem().displayMetrics.heightPixels)

    }
}