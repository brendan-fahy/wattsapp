package brendan.wattsapp

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.os.BatteryManager
import android.preference.PreferenceManager
import android.util.Log
import brendan.wattsapp.Constants.JOB_ID
import brendan.wattsapp.Constants.JOB_INTERVAL

class PowerConnectionReceiver: BroadcastReceiver() {

    val TAG = "PowerConnectionReceiver"

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "onReceive: ")

        // No-op if disabled in sharedprefs
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.SHARED_PREF_ENABLED, false)) {
            Log.d(TAG, "onReceive: disabled in SharedPreferences, returning early.")
            return
        }

        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, JOB_ID, alarmIntent, 0)

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (batteryStatus.shouldShowNotification()) {
            Log.d(TAG, "onReceive: device is charging or unknown, scheduling job.")

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, JOB_INTERVAL, JOB_INTERVAL,
                    pendingIntent)
        } else if (batteryStatus.shouldClearNotification()) {
            Log.d(TAG, "onReceive: device has been disconnected, cancelling all jobs.")

            alarmManager.cancel(pendingIntent)

            // FIXME this is a pretty hacky way of getting the job done :(
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .cancel(Constants.NOTIFICATION_ID)
        }
    }

    fun Intent.shouldShowNotification(): Boolean {
        val status = getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        return status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
                || status == BatteryManager.BATTERY_STATUS_UNKNOWN
    }

    fun Intent.shouldClearNotification(): Boolean {
        return (getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                == BatteryManager.BATTERY_STATUS_DISCHARGING)
    }
}