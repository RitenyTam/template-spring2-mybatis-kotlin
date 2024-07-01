package com.riteny.util.exception.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "exception.handler")
data class CommonExceptionProperties(var isNeedInternationalization: Boolean = false, var profileType: Int = 0) {
    fun setIsNeedInternationalization(isNeedInternationalization: Boolean) {
        this.isNeedInternationalization = isNeedInternationalization
    }
}