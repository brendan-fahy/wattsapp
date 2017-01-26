package brendan.wattsapp

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class WearDisplayActivity : Activity() {

    private var mTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        mTextView = findViewById(R.id.text) as TextView
    }
}
