package com.htphtp.tools

//fun <T: Any?> List<T>?.isNullOrEmpty() = null == this || this.isEmpty()

fun <T: Any?> List<T>?.isNotNullAndNotEmpty() = !(null == this || this.isEmpty())

fun <T: Any?> List<T>?.asArrayList() = if (this is ArrayList) this else null