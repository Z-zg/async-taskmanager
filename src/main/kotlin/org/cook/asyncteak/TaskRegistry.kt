package org.cook.asyncteak

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

object TaskRegistry {
    private val tasks: ConcurrentHashMap<String, TaskInfo> = ConcurrentHashMap()

    fun registerTask(taskId: String, moduleName: String, taskName: String) {
        val taskInfo = TaskInfo(taskId, moduleName, taskName)
        tasks[taskId] = taskInfo
        println("TaskRegistry: Task '$taskName' (ID: $taskId) from module '$moduleName' registered. Status: ${taskInfo.status}")
    }

    fun markTaskRunning(taskId: String, logFilePath: String) {
        tasks[taskId]?.apply {
            this.status = TaskStatus.RUNNING
            this.logFilePath = logFilePath
            println("TaskRegistry: Task '$taskName' (ID: $taskId) marked as RUNNING. Log: $logFilePath")
        } ?: println("TaskRegistry: Warning - Task with ID $taskId not found for marking as RUNNING.")
    }

    fun markTaskSuccess(taskId: String) {
        tasks[taskId]?.apply {
            this.status = TaskStatus.SUCCESS
            this.endTime = System.currentTimeMillis()
            println("TaskRegistry: Task '$taskName' (ID: $taskId) marked as SUCCESS.")
        } ?: println("TaskRegistry: Warning - Task with ID $taskId not found for marking as SUCCESS.")
    }

    fun markTaskFailed(taskId: String, errorMessage: String) {
        tasks[taskId]?.apply {
            this.status = TaskStatus.FAILED
            this.endTime = System.currentTimeMillis()
            this.errorMessage = errorMessage
            println("TaskRegistry: Task '$taskName' (ID: $taskId) marked as FAILED. Error: $errorMessage")
        } ?: println("TaskRegistry: Warning - Task with ID $taskId not found for marking as FAILED.")
    }

    fun updateTaskProgress(taskId: String, progress: TaskProgress) {
        tasks[taskId]?.apply {
            this.latestProgress = progress
            // Optional: Print less frequently for high-volume updates
            // println("TaskRegistry: Task '$taskName' (ID: $taskId) progress: ${progress.percent}% - ${progress.description}")
        } ?: println("TaskRegistry: Warning - Task with ID $taskId not found for progress update.")
    }

    fun getTaskInfo(taskId: String): TaskInfo? {
        return tasks[taskId]
    }

    fun getAllTasks(): List<TaskInfo> {
        return tasks.values.toList()
    }

    fun getTasksByModule(moduleName: String): List<TaskInfo> {
        return tasks.values.filter { it.moduleName == moduleName }
    }

    fun getRunningTasksByModule(moduleName: String): List<TaskInfo> {
        return tasks.values.filter { it.moduleName == moduleName && it.status == TaskStatus.RUNNING }
    }

    fun getCompletedTasksByModule(moduleName: String): List<TaskInfo> {
        return tasks.values.filter { it.moduleName == moduleName && (it.status == TaskStatus.SUCCESS || it.status == TaskStatus.FAILED) }
    }

    // Quick extension interface example
    fun getTasksByModuleAndStatus(moduleName: String, status: TaskStatus): List<TaskInfo> {
        return tasks.values.filter { it.moduleName == moduleName && it.status == status }
    }
} 