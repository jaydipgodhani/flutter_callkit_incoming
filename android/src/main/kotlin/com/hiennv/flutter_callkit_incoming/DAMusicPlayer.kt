package com.hiennv.flutter_callkit_incoming

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.TextUtils
import android.util.Log


class DAMusicPlayer private constructor() {
    companion object {

        @Volatile
        private lateinit var instance: DAMusicPlayer

        private var vibrator: Vibrator? = null
        private var audioManager: AudioManager? = null

        private var mediaPlayer: MediaPlayer? = null

        fun getInstance(): DAMusicPlayer {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = DAMusicPlayer()
                }
                return instance
            }
        }
    }

    fun startMusicPlayer(filePath: String, context: Context, applicationContext: Context) {
        Log.d("Callkit", "fetchDogResponse: $filePath")
        this.prepare(context)
        this.playSound(filePath, context, applicationContext)
        this.playVibrator(context)
    }

    private fun prepare(context: Context) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        vibrator?.cancel()
    }
    fun stopAllPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        vibrator?.cancel()
    }
    private fun playVibrator(context: Context) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (audioManager?.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> {
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0L, 1000L, 1000L), 0))
                } else {
                    vibrator?.vibrate(longArrayOf(0L, 1000L, 1000L), 0)
                }
            }
        }
    }

    private fun playSound(sound: String?, context: Context, applicationContext: Context) {
        /* this.data = intent?.extras
         val sound = this.data?.getString(
             CallkitIncomingBroadcastReceiver.EXTRA_CALLKIT_RINGTONE_PATH,
             ""
         )*/
        var uri = sound?.let { getRingtoneUri(it,context) }
        if (uri == null) {
            uri = RingtoneManager.getActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE
            )
        }
        try {
            mediaPlayer(applicationContext, uri!!)
        } catch (e: Exception) {
            try {
                uri = getRingtoneUri("ringtone_default", context)
                mediaPlayer(applicationContext, uri!!)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    private fun mediaPlayer(applicationContext: Context, uri: Uri) {
        mediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attribution = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setLegacyStreamType(AudioManager.STREAM_RING)
                .build()
            mediaPlayer?.setAudioAttributes(attribution)
        } else {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_RING)
        }
        val assetFileDescriptor = applicationContext.getContentResolver().openAssetFileDescriptor(uri, "r")
        if (assetFileDescriptor != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaPlayer?.setDataSource(assetFileDescriptor)
            }else{
                mediaPlayer?.setDataSource(applicationContext, uri)
            }
        } else {
            mediaPlayer?.setDataSource(applicationContext, uri)
        }
        mediaPlayer?.prepare()
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun getRingtoneUri(fileName: String, context: Context) = try {
        if (TextUtils.isEmpty(fileName)) {
            RingtoneManager.getActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE
            )
        }
        val resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        if (resId != 0) {
            Uri.parse("android.resource://${context.packageName}/$resId")
        } else {
            if (fileName.equals("system_ringtone_default", true)) {
                RingtoneManager.getActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE
                )
            } else {
                RingtoneManager.getActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE
                )
            }
        }
    } catch (e: Exception) {
        try {
            if (fileName.equals("system_ringtone_default", true)) {
                RingtoneManager.getActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE
                )
            } else {
                RingtoneManager.getActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE
                )
            }
        } catch (e: Exception) {
            null
        }
    }

}