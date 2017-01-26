package brendan.wattsapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Example shell activity which simply broadcasts to our receiver and exits.
 */
class WearStubBroadcastActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val i = Intent()
        i.action = "brendan.wattsapp.SHOW_NOTIFICATION"
        i.putExtra(WearBatteryReceiver.CONTENT_KEY, getString(R.string.title))
        sendBroadcast(i)
        finish()
    }
}
