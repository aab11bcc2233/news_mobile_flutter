package com.htphtp.tools.image

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * Created by htp on 2018/5/23.
 */
fun Bitmap.toBytes(maxSize: Int = 0, needRecycle: Boolean): ByteArray {
    val output = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, output)
    if (maxSize > 0) {
        var options = 100
        while (output.toByteArray().size > maxSize && options != 10) {
            output.reset() //清空output
            this.compress(Bitmap.CompressFormat.JPEG, options, output)//这里压缩options%，把压缩后的数据存放到output中
            options -= 10
        }

        if (needRecycle) {
            this.recycle()
        }
    }

    return output.toByteArray()
}

fun Bitmap.toShareThumbBytes(needRecycle: Boolean): ByteArray = this.toBytes(32768, needRecycle)