package com.riteny.util.exception.config

import com.riteny.util.exception.internationalization.InternationalizationDatasource
import com.riteny.util.exception.internationalization.impl.InMemoryInternationalizationDatasource
import com.riteny.util.exception.internationalization.impl.PropertiesFileInternationalizationDatasource
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ExceptionHandlerConfiguration {

    @Bean
    @ConditionalOnExpression("\${exception.handler.profile-type:0} == 1 && \${exception.handler.is-need-internationalization}")
    open fun memoryInternationalizationDatasource(): InternationalizationDatasource {
        return InMemoryInternationalizationDatasource()
    }

    @Bean
    @ConditionalOnExpression("\${exception.handler.profile-type:0} == 2 && \${exception.handler.is-need-internationalization}")
    open fun propertiesInternationalizationDatasource(): InternationalizationDatasource {
        return PropertiesFileInternationalizationDatasource()
    }

    @Bean
    @ConditionalOnExpression("!\${exception.handler.is-need-internationalization}")
    open fun defaultInternationalizationDatasource(): InternationalizationDatasource {
        return object : InternationalizationDatasource {
            override fun getValue(index: String, lang: String): String {
                return index
            }
        }
    }
}