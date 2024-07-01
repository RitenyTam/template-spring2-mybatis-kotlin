package com.riteny.util.exception.core

import com.riteny.logger.CommonLoggerFactory
import com.riteny.util.exception.core.api.CommonApiExceptionHandlerAgent
import com.riteny.util.exception.core.exception.CommonApiException
import com.riteny.util.exception.core.exception.CommonException
import com.riteny.util.exception.core.exception.CommonViewException
import com.riteny.util.exception.core.view.CommonViewExceptionHandlerAgent
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class CommonExceptionHandler @Autowired constructor(
    private val commonApiExceptionHandlerAgent: CommonApiExceptionHandlerAgent,
    private val commonViewExceptionHandlerAgent: CommonViewExceptionHandlerAgent
) {

    companion object {
        val logger: Logger = CommonLoggerFactory.getLogger("api")
    }

    @ExceptionHandler(value = [CommonApiException::class])
    @ResponseBody
    fun commonApiExceptionHandler(
        request: HttpServletRequest, response: HttpServletResponse, e: CommonException
    ): Any? {

        logger.error("Before exception handler : ${e.message}")
        val result = commonApiExceptionHandlerAgent.handleException(e, request, response)
        logger.error("After exception handler : ${e.errorMsg}", e)
        logger.error("url : ${request.requestURL} method : ${request.method} IP : ${request.remoteAddr} response : $result")

        return result
    }

    @ExceptionHandler(value = [CommonViewException::class])
    @ResponseBody
    fun commonViewExceptionHandler(request: HttpServletRequest, response: HttpServletResponse, e: CommonException) {
        logger.error("Before exception handler : ${e.message}")
        commonViewExceptionHandlerAgent.handleException(e, request, response)
        logger.error("After exception handler : ${e.errorMsg}", e)
        logger.error("url : ${request.requestURL} method : ${request.method} IP : ${request.remoteAddr}")
    }
}