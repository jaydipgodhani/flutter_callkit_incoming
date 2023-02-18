package com.hiennv.flutter_callkit_incoming

import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.hiennv.flutter_callkit_incoming.UserDataUploadWorker
import android.util.Log
import androidx.work.Data

class WorkUtil private constructor() {
    init {
        mWorkManager = WorkManager.getInstance()
    }

    companion object {
        private lateinit var mWorkManager: WorkManager
        var workUtil: WorkUtil? = null
        val instance: WorkUtil?
            get() {
                if (workUtil == null) {
                    workUtil = WorkUtil()
                }
                return workUtil
            }

        fun startSyncing(data:Data) {
            val compressionWork = OneTimeWorkRequest.Builder(UserDataUploadWorker::class.java)
            compressionWork.setInputData(data)
            mWorkManager.enqueue(compressionWork.build())
        }

        fun cancelAllWork() {
            Log.d("DECLINE", "fetchDogResponse: 11")
            mWorkManager.cancelAllWork()
        }
    }
}