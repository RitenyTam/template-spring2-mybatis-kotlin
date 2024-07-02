package com.riteny.config

import com.riteny.logger.CommonLoggerFactory.getLogger
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.slf4j.Logger
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

/**
 * @author Riteny
 * 2020/1/13  13:49
 */
class ApiLogAspect : MethodInterceptor {
    private val logger: Logger = getLogger("api")

    @Throws(Throwable::class)
    override fun invoke(methodInvocation: MethodInvocation): Any? {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes

        val request = attributes.request

        //记录执行开始时间，访问路径，入参，日期和访问者IP信息等
        val startTime = System.currentTimeMillis()
        logger.info("#Request :${request.requestURL} #Method : ${request.method} #Remote IP : ${request.remoteAddr} #Param : ${listOf(*methodInvocation.arguments)}")
        try {
            val result = methodInvocation.proceed()
            //记录执行完成后，返回的参数
            logger.info("#Response : ${request.requestURL} #Method : ${request.method} #Remote IP : ${request.remoteAddr} #Result : $result #Finished : ${System.currentTimeMillis() - startTime}ms")
            return result
        } catch (e: Exception) {
            //记录异常时的原因
            logger.info("#Response : ${request.requestURL} #Method : ${request.method} #Remote IP : ${request.remoteAddr} #Result : ${e.message} #Finished : ${System.currentTimeMillis() - startTime}ms")
            throw e
        }
    }
}
