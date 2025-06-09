package org.cook.asyncteak

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import org.slf4j.LoggerFactory
import java.io.File
import java.util.UUID

class TaskExecutor {

    fun runTask(task: AbstractTask, moduleName: String, taskName: String) {
        val taskId = UUID.randomUUID().toString()
        TaskRegistry.registerTask(taskId, moduleName, taskName)

        val logFilePath = "logs/task-$taskId.log"
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val taskLogger = loggerContext.getLogger("TaskLogger-" + taskId)

        // Create and configure FileAppender for this task
        val fileAppender = FileAppender<ILoggingEvent>()
        fileAppender.context = loggerContext
        fileAppender.name = "file-" + taskId
        fileAppender.file = logFilePath
        fileAppender.isAppend = true

        val encoder = PatternLayoutEncoder()
        encoder.context = loggerContext
        encoder.pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        encoder.start()

        fileAppender.encoder = encoder
        fileAppender.start()

        taskLogger.addAppender(fileAppender)
        taskLogger.level = Level.INFO
        taskLogger.isAdditive = false // Prevent logging to root logger

        task.setTaskContext(taskId, moduleName, taskName)

        TaskRegistry.markTaskRunning(taskId, logFilePath)
        println("TaskExecutor: Starting task '$taskName' (ID: $taskId).")

        try {
            task.execute()
            TaskRegistry.markTaskSuccess(taskId)
            println("TaskExecutor: Task '$taskName' (ID: $taskId) completed successfully.")
        } catch (e: Exception) {
            TaskRegistry.markTaskFailed(taskId, e.message ?: "Unknown error")
            System.err.println("TaskExecutor: Task '$taskName' (ID: $taskId) failed with error: ${e.message}")
            e.printStackTrace()
        } finally {
            // Stop and remove the appender
            fileAppender.stop()
            taskLogger.detachAppender(fileAppender)
            println("TaskExecutor: Stopped logging for task '$taskName' (ID: $taskId).")
        }
    }
} 