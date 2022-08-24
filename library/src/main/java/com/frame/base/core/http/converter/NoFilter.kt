package com.frame.base.core.http.converter

import java.lang.reflect.Type

/**
 *Desc:
 *Author:Zhu
 *Date:2022/7/12
 */
class NoFilter private constructor() {
    private val maps = HashMap<String, Int>()
    fun registerNoFilter(name: String) {
        maps.put(name, 1)
    }

    fun isFilter(type: Type): Boolean {
        return if (type is Class<*>)!maps.contains(type.name) else false
    }

    companion object {
        @JvmStatic
        fun instance(): NoFilter {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = NoFilter()
    }
}