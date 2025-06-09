package org.cook.asyncteak

import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.util.StatusPrinter
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    // Configure Logback programmatically to ensure logs directory is created
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val configurator = JoranConfigurator()
    configurator.context = loggerContext
    loggerContext.reset()

    // Create logs directory if it doesn't exist
    val logsDir = File("logs")
    if (!logsDir.exists()) {
        logsDir.mkdirs()
    }

    // You can also load a logback.xml here if needed
    // configurator.doConfigure("src/main/resources/logback.xml")

    StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext)

    println("\n--- Distributed Task Scheduling System Starting ---\n")

    val taskExecutor = TaskExecutor()
    val executorService = Executors.newFixedThreadPool(5) // Simulate 5 concurrent tasks

    val modules = listOf("DataProcessing", "ReportGeneration", "SystemMaintenance")
    val taskNames = listOf("TaskA", "TaskB", "TaskC", "TaskD", "TaskE", "TaskF")

    // Schedule some demo tasks
    for (i in 1..10) {
        val moduleName = modules[i % modules.size]
        val taskName = taskNames[i % taskNames.size]
        executorService.submit { taskExecutor.runTask(DemoTask(), moduleName, taskName) }
        Thread.sleep(200) // Stagger task submissions slightly
    }

    // Monitor tasks periodically
    val monitorScheduler = Executors.newSingleThreadScheduledExecutor()
    monitorScheduler.scheduleAtFixedRate({
        println("\n--- Task Status Update (${System.currentTimeMillis()}) ---")
        modules.forEach { module ->
            val runningTasks = TaskRegistry.getRunningTasksByModule(module)
            if (runningTasks.isNotEmpty()) {
                println("Module: $module - Running Tasks:")
                runningTasks.forEach { task ->
                    val progress = task.latestProgress
                    val progressInfo = if (progress != null) {
                        "${progress.percent}% - ${progress.description} (Processed: ${progress.currentProcessed}/${progress.totalToProcess})"
                    } else {
                        "No progress reported"
                    }
                    println("  - ${task.taskName} (ID: ${task.taskId.substring(0, 8)}...): Status: ${task.status}, Progress: $progressInfo")
                }
            }

            val completedTasks = TaskRegistry.getCompletedTasksByModule(module)
            if (completedTasks.isNotEmpty()) {
                println("Module: $module - Completed Tasks:")
                completedTasks.forEach { task ->
                    val statusDetail = if (task.status == TaskStatus.FAILED) "FAILED: ${task.errorMessage}" else "SUCCESS"
                    println("  - ${task.taskName} (ID: ${task.taskId.substring(0, 8)}...): Status: $statusDetail")
                }
            }
        }
        println("-------------------------------------------")
    }, 0, 5, TimeUnit.SECONDS)

    // Shutdown gracefully
    executorService.shutdown()
    executorService.awaitTermination(10, TimeUnit.MINUTES)
    monitorScheduler.shutdown()

    println("\n--- All tasks completed or timed out. System Shutting Down.---")
} 