package com.riteny.util.exception.core.api

import com.riteny.util.exception.core.exception.CommonException
import java.lang.reflect.ParameterizedType
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface CommonApiExceptionHandler<E : CommonException,Response> {

    fun handler(e: E, request: HttpServletRequest, response: HttpServletResponse): Response

    fun getExceptionClass(): String {

        val clazz = this.javaClass

        val interfaceTypes = clazz.genericInterfaces

        for (interfaceType in interfaceTypes) {

            val pt = interfaceType as ParameterizedType

            if (pt.rawType.typeName.equals(CommonApiExceptionHandler::class.java.typeName)) {
                val actualTypeArguments = pt.actualTypeArguments
                return actualTypeArguments[0].typeName
            }
        }

        throw RuntimeException("Can not find exception type .")
    }
}