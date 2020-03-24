package com.htphtp.tools.reflex

import java.lang.reflect.Modifier

fun Any.reflexFieldsMap(isFilterTransient: Boolean = true, isFilterStatic: Boolean = true): Map<String, Any?> {
    return javaClass.declaredFields
            .filter { isFilterTransient && !Modifier.toString(it.modifiers).contains("transient") }
            .filter { !it.toString().contains("serialVersionUID") }
            .filter { isFilterStatic && !it.toString().contains("static") }
            .map {
                it.isAccessible = true
                it.name to it.get(this)
            }.toMap()
}

fun Map<String, Any?>.mapValuesToString(): Map<String, String> {
    return filter { it.value != null }
            .map { it.key to it.value.toString() }
            .toMap()
}