package org.cook.asyncteak

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractTask {

    lateinit var taskId: String
    lateinit var moduleName: String
    lateinit var taskName: String
    protected lateinit var taskLogger: Logger

    fun setTaskContext(taskId: String, moduleName: String, taskName: String) {
        this.taskId = taskId
        this.moduleName = moduleName
        this.taskName = taskName
        this.taskLogger = LoggerFactory.getLogger("TaskLogger-" + taskId)
    }

    protected fun reportProgress(
        percent: Int,
        description: String,
        currentProcessed: Long = 0,
        totalToProcess: Long = 0
    ) {
        if (!::taskId.isInitialized) {
            throw IllegalStateException("Task context not initialized. Call setTaskContext first.")
        }
        TaskProgressReporter.reportProgress(taskId, percent, description, currentProcessed, totalToProcess)
    }

    protected fun logInfo(message: String) {
        if (::taskLogger.isInitialized) {
            taskLogger.info(message)
        } else {
            System.err.println("Logger not initialized for task $taskId: $message")
        }
    }

    protected fun logWarn(message: String) {
        if (::taskLogger.isInitialized) {
            taskLogger.warn(message)
        } else {
            System.err.println("Logger not initialized for task $taskId: $message")
        }
    }

    protected fun logError(message: String, throwable: Throwable? = null) {
        if (::taskLogger.isInitialized) {
            if (throwable != null) {
                taskLogger.error(message, throwable)
            } else {
                taskLogger.error(message)
            }
        } else {
            System.err.println("Logger not initialized for task $taskId: $message")
            throwable?.printStackTrace()
        }
    }

    abstract fun execute()
} 