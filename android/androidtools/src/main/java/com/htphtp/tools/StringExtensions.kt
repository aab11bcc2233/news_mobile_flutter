package com.htphtp.tools

import java.util.regex.Pattern

fun String.isAllAreSpaces() = this.isNotEmpty() && this.trim().isEmpty()

fun String.isDoubleOrFloat(): Boolean {
    if (isEmpty()) {
        return false
    }
    val pattern = Pattern.compile("^[-\\+]?[.\\d]*$")
    return pattern.matcher(this).matches()
}


fun String.isInteger(): Boolean {
    if (isEmpty()) {
        return false
    }

    val pattern = Pattern.compile("^[-\\+]?[\\d]*$")
    return pattern.matcher(this).matches()
}