package com.riteny.logger

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.util.FileSize
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

/**
 * @author Riteny
 */
object CommonLoggerFactory {

    private val loggers: MutableMap<String, Logger> = HashMap()

    fun getLogger(name: String): Logger {

        var logger: Logger

        synchronized(CommonLoggerFactory::class.java) {
            logger = loggers[name]?:createLogger(name)
            loggers.put(name, logger)
        }

        return logger
    }

    fun registerLogger(loggerName: String, baseDir: String, maxHistory: Int, maxFileSize: String) {
        CommonLogProperties.put(loggerName, baseDir, maxHistory, maxFileSize)
    }

    private fun createLogger(loggerName: String): Logger {
        val configEntity = CommonLogProperties.get(loggerName)

        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

        val logger = loggerContext.getLogger(loggerName)

        val rollingFileAppender = getInfoAppender(loggerName, loggerContext, configEntity)
        val errorRollingFileAppender = getErrorAppender(loggerName, loggerContext, configEntity)
        logger.addAppender(rollingFileAppender)
        logger.addAppender(errorRollingFileAppender)

        return logger
    }


    private fun getInfoAppender(
        loggerName: String,
        loggerContext: LoggerContext,
        configEntity: CommonLogConfigEntity
    ): RollingFileAppender<ILoggingEvent> {
        val rollingFileAppender = RollingFileAppender<ILoggingEvent>()
        rollingFileAppender.context = loggerContext
        rollingFileAppender.isAppend = true
        rollingFileAppender.name = loggerName + "LogAppender"
        rollingFileAppender.file = configEntity.baseDir + "/" + loggerName + ".info.log"

        val rollingPolicy = getRollingPolicy(loggerName, "info", loggerContext, configEntity, rollingFileAppender)
        rollingFileAppender.rollingPolicy = rollingPolicy

        val levelFilter = LevelFilter()
        levelFilter.setLevel(Level.ERROR)
        levelFilter.onMatch = FilterReply.DENY
        levelFilter.onMismatch = FilterReply.ACCEPT
        levelFilter.start()
        rollingFileAppender.addFilter(levelFilter)

        val encoder = getPatternLayoutEncoder(loggerContext)
        rollingFileAppender.encoder = encoder
        rollingFileAppender.start()

        return rollingFileAppender
    }


    private fun getErrorAppender(
        loggerName: String,
        loggerContext: LoggerContext, configEntity: CommonLogConfigEntity
    ): RollingFileAppender<ILoggingEvent> {
        val rollingFileAppender = RollingFileAppender<ILoggingEvent>()
        rollingFileAppender.context = loggerContext
        rollingFileAppender.isAppend = true
        rollingFileAppender.name = loggerName + "ErrorLogAppender"
        rollingFileAppender.file = configEntity.baseDir + "/" + loggerName + ".error.log"

        val rollingPolicy = getRollingPolicy(loggerName, "error", loggerContext, configEntity, rollingFileAppender)
        rollingFileAppender.rollingPolicy = rollingPolicy

        val levelFilter = LevelFilter()
        levelFilter.setLevel(Level.ERROR)
        levelFilter.onMatch = FilterReply.ACCEPT
        levelFilter.onMismatch = FilterReply.DENY
        levelFilter.start()
        rollingFileAppender.addFilter(levelFilter)

        val encoder = getPatternLayoutEncoder(loggerContext)
        rollingFileAppender.encoder = encoder
        rollingFileAppender.start()

        return rollingFileAppender
    }

    private fun getRollingPolicy(
        loggerName: String,
        loggerLevel: String,
        loggerContext: LoggerContext,
        configEntity: CommonLogConfigEntity,
        rollingFileAppender: RollingFileAppender<ILoggingEvent>
    ): SizeAndTimeBasedRollingPolicy<Any> {
        val rollingPolicy = SizeAndTimeBasedRollingPolicy<Any>()
        rollingPolicy.fileNamePattern =
            configEntity.baseDir + "/" + loggerName + "." + loggerLevel + ".%d{yyyy-MM-dd}.%i.log"
        rollingPolicy.maxHistory = configEntity.maxHistory!!
        rollingPolicy.context = loggerContext
        rollingPolicy.setParent(rollingFileAppender)
        rollingPolicy.setMaxFileSize(FileSize.valueOf(configEntity.maxFileSize))
        rollingPolicy.isCleanHistoryOnStart = true
        rollingPolicy.start()

        return rollingPolicy
    }


    private fun getPatternLayoutEncoder(loggerContext: LoggerContext): PatternLayoutEncoder {
        val encoder = PatternLayoutEncoder()
        encoder.pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %logger{50} %msg%n"
        encoder.charset = StandardCharsets.UTF_8
        encoder.context = loggerContext
        encoder.start()

        return encoder
    }
}
