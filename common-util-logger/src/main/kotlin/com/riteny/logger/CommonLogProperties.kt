package com.riteny.logger

/**
 * @author Riteny
 */
object CommonLogProperties {
    private const val baseDir = "logs"

    private const val maxHistory = 30

    private const val maxFileSize = "50MB"

    private val config: MutableMap<String, CommonLogConfigEntity> = HashMap()

    fun get(loggerName: String): CommonLogConfigEntity {
        var configEntity = config[loggerName]

        if (configEntity == null) {
            configEntity = CommonLogConfigEntity(baseDir, maxHistory, maxFileSize)
        }

        return configEntity
    }

    fun put(loggerName: String, baseDir: String, maxHistory: Int, maxFileSize: String) {
        val configEntity = CommonLogConfigEntity(baseDir, maxHistory, maxFileSize)
        config[loggerName] = configEntity
    }
}
