package brendan.wattsapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import brendan.wattsapp.Constants.JOB_ID
import brendan.wattsapp.Constants.JOB_INTERVAL

class PowerConnectionReceiver: BroadcastReceiver() {

    val TAG = "PowerConnectionReceiver"

    override fun onReceive(context: Context, intent: Intent) {

        val alarmIntent = Intent(context, ShowAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, JOB_ID, alarmIntent, 0)

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (batteryStatus.shouldShowNotification()) {
            Log.d(TAG, "onReceive: device is charging, scheduling job.")

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, JOB_INTERVAL,
                    pendingIntent)
        } else if (batteryStatus.shouldClearNotification()) {
            Log.d(TAG, "onReceive: device has been disconnected, cancelling all jobs.")

            alarmManager.cancel(pendingIntent)
            (context.applicationContext.getSystemService(SERVICE_NOTIFICATION_HANDLER) as NotificationHandler)
                    .cancelNotification()
        }
    }

    fun Intent.shouldShowNotification(): Boolean {
        val status = getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        return status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
    }

    fun Intent.shouldClearNotification(): Boolean {
        val status = getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        return status == BatteryManager.BATTERY_STATUS_DISCHARGING
                || status == BatteryManager.BATTERY_STATUS_UNKNOWN
    }
}