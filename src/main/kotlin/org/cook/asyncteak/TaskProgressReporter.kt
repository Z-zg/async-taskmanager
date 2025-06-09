package org.cook.asyncteak

import kotlin.jvm.JvmOverloads

object TaskProgressReporter {

    @JvmOverloads
    fun reportProgress(
        taskId: String,
        percent: Int,
        description: String,
        currentProcessed: Long = 0,
        totalToProcess: Long = 0
    ) {
        val progress = TaskProgress(
            taskId = taskId,
            percent = percent,
            description = description,
            currentProcessed = currentProcessed,
            totalToProcess = totalToProcess
        )
        TaskRegistry.updateTaskProgress(taskId, progress)
    }
} 