package com.htphtp.tools

/**
 *
 * Created by TP on 2019/2/16
 *
 */

fun Double.isInteger(): Boolean {
    val obj = this

    val eps = 1e-10  // 精度范围
    return obj - Math.floor(obj) < eps
}