package com.rohit.voicebotcaller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            Log.d("CallReceiver", "Incoming call detected")

            val serviceIntent = Intent(context, CallService::class.java)
            serviceIntent.action = "CALL_INCOMING"
            context.startService(serviceIntent)
        }
    }
}
