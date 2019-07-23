package com.fcen.seguridad.BaseService

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

class BaseService(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("JUANI", "el service esta corriendo")
        return Result.success()
    }

    companion object {
        fun scheduleWorker(): UUID { //UUID is the ID of the worker
            val constraints = Constraints.Builder().build()

            val  worker = PeriodicWorkRequestBuilder<BaseService>(100, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance().enqueue(worker)
            return worker.id
        }
    }
}