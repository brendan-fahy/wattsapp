package brendan.wattsapp

import android.app.Notification
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.support.v4.app.NotificationCompat
import android.util.Log

class PowerService: JobService() {

    private val TAG = "PowerService"

    lateinit var notificationManager: NotificationManager

    override fun onStartJob(jobParams: JobParameters): Boolean {
        Log.d(TAG, "onStartJob:")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = registerReceiver(null, intentFilter)

        Log.d(TAG, "onStartJob: sending notification with battery level " +
                "${batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)}")
        notificationManager.notify(Constants.NOTIFICATION_ID, buildNotification(batteryStatus))

        jobFinished(jobParams, false)
        return false
    }

    private fun buildNotification(batteryStatus: Intent): Notification {
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

        return NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Phone is $level% charged.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOnlyAlertOnce(true)
                .build()
    }

    override fun onStopJob(jobParams: JobParameters): Boolean {
        Log.d(TAG, "onStopJob: ")
        clearNotification()

        /** "True to indicate to the JobManager whether you'd like to reschedule this job based on
         * the retry criteria provided at job creation-time. False to drop the job. Regardless of
         * value returned, your job must stop executing."
         */
        return true
    }

    private fun clearNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(Constants.NOTIFICATION_ID)
    }
}