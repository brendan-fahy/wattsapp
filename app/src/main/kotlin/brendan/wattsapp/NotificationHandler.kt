package brendan.wattsapp

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.BatteryManager
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log

class NotificationHandler(val context: Context) {

    private val TAG = "NotificationHandler"

    private var batteryLevel: Int? = null

    private val notificationManager: NotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val storage: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun showNotification() {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

        if (shouldShowNotification(level)) {
            batteryLevel = level

            Log.d(TAG, "onReceive: sending notification with battery level $batteryLevel")

            notificationManager.notify(Constants.NOTIFICATION_ID,
                    buildNotification(context))
        }
    }

    private fun shouldShowNotification(newBatteryLevel: Int): Boolean {
        return notificationsAreEnabled() && batteryLevelHasChanged(newBatteryLevel)
    }

    private fun notificationsAreEnabled() = storage.getBoolean(context.getString(R.string.prefs_enable_key), true)

    private fun batteryLevelHasChanged(newBatteryLevel: Int) = newBatteryLevel != batteryLevel

    private fun buildNotification(context: Context): Notification {
        var builder = NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Phone is $batteryLevel% charged.")
                .setSmallIcon(R.mipmap.ic_notification)
                .setOnlyAlertOnce(true)

        if (shouldAlert()) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            builder = builder
                    .setVibrate(longArrayOf(100L, 100L, 100L))
                    .setSound(alarmSound)
        }

        return builder.build()
    }

    private fun shouldAlert() = batteryLevel == 100 && storage.getBoolean(
            context.getString(R.string.prefs_alert_on_full_key),
            false)

    fun cancelNotification() {
        notificationManager.cancel(Constants.NOTIFICATION_ID)
    }
}