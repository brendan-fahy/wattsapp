package brendan.wattsapp

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.BatteryManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log

class PowerConnectionReceiver: BroadcastReceiver() {

    val TAG = "PowerConnectionReceiver"

    private lateinit var jobScheduler: JobScheduler

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "onReceive: ")

        // No-op if disabled in sharedprefs
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.SHARED_PREF_ENABLED, false)) {
            Log.d(TAG, "onReceive: disabled in SharedPreferences, returning early.")
            return
        }

        jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val isCharging = ((status == BatteryManager.BATTERY_STATUS_CHARGING) ||
                status == BatteryManager.BATTERY_STATUS_FULL)
        val isUnknown = (status == BatteryManager.BATTERY_STATUS_UNKNOWN)
        val hasBeenDisconnected = (status == BatteryManager.BATTERY_STATUS_DISCHARGING)

        if (isCharging || isUnknown) {
            Log.d(TAG, "onReceive: device is charging or unknown, scheduling job.")
            jobScheduler.schedule(jobInfo(context))
        } else if (hasBeenDisconnected) {
            Log.d(TAG, "onReceive: device has been disconnected, cancelling all jobs.")
            jobScheduler.cancelAll()
        }
        Log.d(TAG, "onReceive: EXTRA_STATUS is $status")
    }

    private fun jobInfo(context: Context): JobInfo {
        val serviceName = ComponentName(context, PowerService::class.java)
        val jobInfoBuilder = JobInfo.Builder(Constants.JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setRequiresCharging(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfoBuilder.setPeriodic(Constants.JOB_INTERVAL, Constants.JOB_FLEX)
        } else {
            jobInfoBuilder.setPeriodic(Constants.JOB_INTERVAL)
        }

        return jobInfoBuilder.build()
    }
}