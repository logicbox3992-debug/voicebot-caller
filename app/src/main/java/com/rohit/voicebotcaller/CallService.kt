package com.rohit.voicebotcaller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CallService : Service() {

    companion object {
        const val CHANNEL_ID = "voicebot_channel"
    }

    private var recorder: MediaRecorder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()

        startForeground(1, buildNotification("Waiting for Calls"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == "CALL_INCOMING") {
            Log.d("CallService", "Handling incoming call")
            answerCall()
            recordCall()
        }

        return START_STICKY
    }

    private fun answerCall() {
        try {
            val tm = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.acceptRingingCall()
            }
        } catch (e: Exception) {
            Log.e("CallService", "Error answering call: ${e.message}")
        }
    }

    private fun recordCall() {
        stopRecording()

        val dir = File(getExternalFilesDir(null), "recordings")
        if (!dir.exists()) dir.mkdirs()

        val file = File(
            dir,
            "rec_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.m4a"
        )

        try {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            updateNotification("Recording…")

        } catch (e: Exception) {
            Log.e("CallService", "Recorder error: ${e.message}")
        }
    }

    private fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (_: Exception) {}
        recorder = null
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VoiceBot Caller",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VoiceBot Caller")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NotificationManager::class.java) as NotificationManager
        nm.notify(1, buildNotification(text))
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
