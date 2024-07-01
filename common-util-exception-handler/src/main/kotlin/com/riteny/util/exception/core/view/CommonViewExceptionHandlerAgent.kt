package com.riteny.util.exception.core.view

import com.riteny.util.exception.config.CommonExceptionProperties
import com.riteny.util.exception.core.exception.CommonException
import com.riteny.util.exception.internationalization.InternationalizationDatasource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class CommonViewExceptionHandlerAgent @Autowired constructor(
    private val commonExceptionProperties: CommonExceptionProperties,
    private val commonApiExceptionHandlers: List<CommonViewExceptionHandler<*>>,
    private val internationalizationDatasource: InternationalizationDatasource
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CommonViewExceptionHandlerAgent::class.java)
    }

    fun handleException(e: CommonException, request: HttpServletRequest, response: HttpServletResponse) {

        if (CollectionUtils.isEmpty(commonApiExceptionHandlers)) {
            logger.warn("List of handler is empty .")
            return
        }

        val exceptionHandler = commonApiExceptionHandlers.firstOrNull { it.getExceptionClass() == e.javaClass.typeName }

        exceptionHandler?.let {

            if (commonExceptionProperties.isNeedInternationalization) {
                val errorMsg = internationalizationDatasource.getValue(e.errorMsg, e.langType)

                if (errorMsg != null) {
                    e.errorMsg = errorMsg
                }
            }

            exceptionHandler as CommonViewExceptionHandler<CommonException>?

            exceptionHandler.handler(e, request, response)
        }
    }
}