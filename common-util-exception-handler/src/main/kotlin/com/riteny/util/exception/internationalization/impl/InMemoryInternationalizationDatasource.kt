package com.riteny.util.exception.internationalization.impl

import com.riteny.util.exception.internationalization.InternationalizationDatasource
import com.riteny.util.exception.internationalization.InternationalizationDatasource.Companion.internationalizationProfileMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class InMemoryInternationalizationDatasource : InternationalizationDatasource {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InMemoryInternationalizationDatasource::class.java)
    }

    fun putValue(index: String, value: String, lang: String) {

        logger.info("Put profile value [ key = {} , value = {} ] to profile key [ {} ]. ", index, value, lang)

        val contextMap: MutableMap<String, String> =
            internationalizationProfileMap.computeIfAbsent(lang) { ConcurrentHashMap() }

        contextMap[index] = value
    }
}