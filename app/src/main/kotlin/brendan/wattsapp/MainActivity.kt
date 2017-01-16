package brendan.wattsapp

import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button


class MainActivity: AppCompatActivity() {

    private lateinit var switchButton: Button

    private var switchButtonEnable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSharedPrefIfFirstRun()

        switchButton = findViewById(R.id.switch_button) as Button

        switchButton.setOnClickListener { view ->
            // Flip the value
            switchButtonEnable = !switchButtonEnable

            // Commit it to storage
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(Constants.SHARED_PREF_ENABLED, switchButtonEnable)
                    .commit()
        }

//        fireBroadcastReceiverExplicitly()
    }

    private fun fireBroadcastReceiverExplicitly() {
        val intent = Intent("brendan.wattsapp.debug")
        sendBroadcast(intent)
    }

    override fun onResume() {
        super.onResume()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        if (jobScheduler.allPendingJobs == null || jobScheduler.allPendingJobs.size > 0) {
            switchButtonEnable = true
            switchButton.setText(R.string.button_action_enable)
        } else {
            switchButtonEnable = false
            switchButton.setText(R.string.button_action_disable)
        }
    }

    private fun setupSharedPrefIfFirstRun() {
        val prefManager= PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefManager.contains(Constants.SHARED_PREF_ENABLED)) {
            prefManager
                    .edit()
                    .putBoolean(Constants.SHARED_PREF_ENABLED, true)
                    .commit()
        }
    }
}