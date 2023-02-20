package com.hiennv.flutter_callkit_incoming

import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.hiennv.flutter_callkit_incoming.UserDataUploadWorker
import android.util.Log
import androidx.work.Data

class WorkUtil private constructor() {

    companion object {
        lateinit var mWorkManager : WorkManager

        @Volatile
        private lateinit var instance: WorkUtil

        fun getInstance(): WorkUtil {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = WorkUtil()
                    mWorkManager= WorkManager.getInstance()
                }
                return instance
            }
        }
    }
    fun startSyncing(data:Data) {
        val compressionWork = OneTimeWorkRequest.Builder(UserDataUploadWorker::class.java)
        compressionWork.setInputData(data)
        mWorkManager.enqueue(compressionWork.build())
    }
    fun cancelAllWork() {
        mWorkManager.cancelAllWork()
    }
}