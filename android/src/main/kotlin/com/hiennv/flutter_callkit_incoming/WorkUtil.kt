package com.hiennv.flutter_callkit_incoming

import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.hiennv.flutter_callkit_incoming.UserDataUploadWorker
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy

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
        var compressionWork = OneTimeWorkRequest.Builder(UserDataUploadWorker::class.java).addTag("RWORK_TAG_NOTES")
        compressionWork.setInputData(data)
        mWorkManager.enqueueUniqueWork("RWORK_TAG_NOTES", ExistingWorkPolicy.KEEP, compressionWork.build())
    }
    fun cancelAllWork() {
        Log.d("DECLINE", "fetchDogResponse: 11")
        /*mWorkManager.cancelAllWork()*/
        mWorkManager.cancelAllWorkByTag("RWORK_TAG_NOTES")
    }
}