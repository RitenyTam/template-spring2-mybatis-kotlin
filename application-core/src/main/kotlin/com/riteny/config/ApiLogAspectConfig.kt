package com.riteny.config

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Riteny
 * 2020/1/13  12:02
 */
@Configuration
class ApiLogAspectConfig {

    @Bean
    fun apiAdviceAdvisor(): AspectJExpressionPointcutAdvisor {
        val advisor = AspectJExpressionPointcutAdvisor()
        advisor.expression = "execution(public * com.riteny.*.controller..*.*(..))"
        advisor.advice = ApiLogAspect()
        return advisor
    }
}
