package brendan.wattsapp

import android.app.Application
import android.preference.PreferenceManager

val SERVICE_NOTIFICATION_HANDLER = "SERVICE_NOTIFICATION_HANDLER"

class WattsApp: Application() {

    val notificationHandler: NotificationHandler by lazy {
        NotificationHandler(this)
    }

    override fun onCreate() {
        super.onCreate()

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

    override fun getSystemService(name: String?): Any {
        if (name == SERVICE_NOTIFICATION_HANDLER) {
            return notificationHandler
        } else {
            return super.getSystemService(name)
        }
    }
}