package brendan.wattsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ShowAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext.getSystemService(SERVICE_NOTIFICATION_HANDLER) as NotificationHandler)
                .showNotification()
    }

}