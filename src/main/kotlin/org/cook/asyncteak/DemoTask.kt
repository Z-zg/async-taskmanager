package org.cook.asyncteak

import kotlin.random.Random

class DemoTask : AbstractTask() {

    override fun execute() {
        logInfo("DemoTask: Starting execution for task '$taskName' (ID: $taskId).")

        val totalSteps = Random.nextInt(5, 15)
        var currentStep = 0
        var success = true

        for (i in 1..totalSteps) {
            if (Random.nextDouble() < 0.1) { // 10% chance of failure
                logError("DemoTask: Simulated failure at step $i.")
                success = false
                throw RuntimeException("Simulated task failure at step $i")
            }

            Thread.sleep(Random.nextLong(100, 500)) // Simulate work
            currentStep++
            val percent = (currentStep.toDouble() / totalSteps * 100).toInt()
            val description = "Processing step $currentStep of $totalSteps"
            reportProgress(percent, description, currentStep.toLong(), totalSteps.toLong())
            logInfo("DemoTask: Progress - $description, ${percent}%")
        }

        if (success) {
            logInfo("DemoTask: Completed successfully for task '$taskName' (ID: $taskId).")
        } else {
            logWarn("DemoTask: Completed with issues for task '$taskName' (ID: $taskId).")
        }
    }
} 