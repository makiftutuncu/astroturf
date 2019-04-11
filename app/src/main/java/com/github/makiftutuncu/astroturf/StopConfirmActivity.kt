package com.github.makiftutuncu.astroturf

import android.app.Activity
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import kotlinx.android.synthetic.main.activity_stop_confirm.*

class StopConfirmActivity : WearableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_confirm)
        setAmbientEnabled()

        btnOk.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
