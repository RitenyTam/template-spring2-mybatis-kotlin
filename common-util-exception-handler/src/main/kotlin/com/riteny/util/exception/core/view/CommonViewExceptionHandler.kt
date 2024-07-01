package com.riteny.util.exception.core.view

import com.riteny.util.exception.core.api.CommonApiExceptionHandler
import com.riteny.util.exception.core.exception.CommonException
import java.lang.reflect.ParameterizedType
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface CommonViewExceptionHandler<E : CommonException> {

    fun handler(e: E, request: HttpServletRequest, response: HttpServletResponse)

    fun getExceptionClass(): String {

        val clazz = this.javaClass

        val interfaceTypes = clazz.genericInterfaces

        for (interfaceType in interfaceTypes) {

            val pt = interfaceType as ParameterizedType

            if (pt.rawType.typeName.equals(CommonViewExceptionHandler::class.java.typeName)) {
                val actualTypeArguments = pt.actualTypeArguments
                return actualTypeArguments[0].typeName
            }
        }

        throw RuntimeException("Can not find exception type .")
    }
}