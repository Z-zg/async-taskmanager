package org.cook.asyncteak

data class TaskProgress(
    val taskId: String,
    val percent: Int,
    val description: String,
    val currentProcessed: Long = 0,
    val totalToProcess: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
) 