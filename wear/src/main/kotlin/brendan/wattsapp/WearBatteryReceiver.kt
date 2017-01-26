package brendan.wattsapp

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class WearBatteryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val displayIntent = Intent(context, WearDisplayActivity::class.java)
        val text = intent.getStringExtra(CONTENT_KEY)
        val notification = Notification.Builder(context).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(text).extend(Notification.WearableExtender().setDisplayIntent(PendingIntent.getActivity(context, 0, displayIntent,
                PendingIntent.FLAG_UPDATE_CURRENT))).build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(0, notification)

        Toast.makeText(context, context.getString(R.string.notification_posted), Toast.LENGTH_SHORT).show()
    }

    companion object {
        val CONTENT_KEY = "contentText"
    }
}
