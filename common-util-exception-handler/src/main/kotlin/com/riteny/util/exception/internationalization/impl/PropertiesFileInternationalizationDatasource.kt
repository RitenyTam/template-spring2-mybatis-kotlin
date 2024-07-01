package com.riteny.util.exception.internationalization.impl

import com.alibaba.fastjson2.JSONObject
import com.riteny.util.exception.internationalization.InternationalizationDatasource
import com.riteny.util.exception.internationalization.InternationalizationDatasource.Companion.internationalizationProfileMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class PropertiesFileInternationalizationDatasource : InternationalizationDatasource {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(PropertiesFileInternationalizationDatasource::class.java)
    }

    init {

        try {
            val resourceAsStream = this.javaClass.getResourceAsStream("/exception-i18n.json")
            if (resourceAsStream == null) {
                logger.error("Read properties for internationalization datasource failed . ")
                throw RuntimeException("Read properties for internationalization datasource failed . ")
            }

            resourceAsStream.bufferedReader().use {

                val bytes = ByteArray(resourceAsStream.available())

                val readSize: Int = resourceAsStream.read(bytes)
                logger.info("Profile size [{}]. ", readSize)

                val jsonStr = String(bytes)
                val propertiesJson: JSONObject = JSONObject.parseObject(jsonStr)

                propertiesJson.forEach { propertiesKey, propertiesValue ->

                    logger.info("Internationalization profile key [ {} ]. ", propertiesKey)
                    val value: JSONObject = JSONObject.parseObject(propertiesValue.toString())

                    value.forEach { profileKey, profileValue ->
                        val contextMap: MutableMap<String, String> =
                            internationalizationProfileMap.computeIfAbsent(propertiesKey) { ConcurrentHashMap() }
                        contextMap[profileKey.toString()] = profileValue.toString()
                    }
                }
            }

            println(internationalizationProfileMap)

        } catch (e: Exception) {
            logger.error("Internationalization datasource init failed . ")
            throw RuntimeException("Internationalization datasource init failed . ", e)
        }
    }


}