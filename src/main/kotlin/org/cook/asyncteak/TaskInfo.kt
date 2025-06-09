package org.cook.asyncteak

data class TaskInfo(
    val taskId: String,
    val moduleName: String,
    val taskName: String,
    var status: TaskStatus = TaskStatus.QUEUED,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var logFilePath: String? = null,
    var errorMessage: String? = null,
    var latestProgress: TaskProgress? = null
) 