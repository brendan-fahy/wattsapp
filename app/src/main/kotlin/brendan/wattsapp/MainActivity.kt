package brendan.wattsapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity: AppCompatActivity() {

    private lateinit var showNotificationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showNotificationButton = findViewById(R.id.showNotificationButton) as Button

        showNotificationButton.setOnClickListener {
            (applicationContext.getSystemService(SERVICE_NOTIFICATION_HANDLER) as NotificationHandler)
                    .showNotification()
        }

        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, SettingsFragment(), SettingsFragment::class.java.simpleName)
                .commit()
    }
}