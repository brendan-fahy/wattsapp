package brendan.wattsapp

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.support.v4.app.NotificationCompat
import android.util.Log

class AlarmReceiver: BroadcastReceiver() {

    val TAG = "AlarmManager"

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)

        Log.d(TAG, "onReceive: sending notification with battery level " +
                "${batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)}")

        notificationManager.notify(Constants.NOTIFICATION_ID,
                buildNotification(context, batteryStatus))
    }

    private fun buildNotification(context: Context, batteryStatus: Intent): Notification {
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

        return NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Phone is $level% charged.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOnlyAlertOnce(true)
                .build()
    }
}