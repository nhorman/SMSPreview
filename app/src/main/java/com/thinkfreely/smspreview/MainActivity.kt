package com.thinkfreely.smspreview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.RECEIVE_SMS),1)
        //val picker = findViewById<com.skydoves.colorpickerview.ColorPickerView>(R.id.colorPickerView) as com.skydoves.colorpickerview.ColorPickerView
        //picker.setColorListener(object : com.skydoves.colorpickerview.listeners.ColorListener {
         //   override fun onColorSelected(color: Int, fromUser: Boolean) {
           //     Log.i("MainActivity", "SELECTNG COLOR")
             //   val views = RemoteViews(packageName, R.layout.s_m_s_preview_widget)
            //}
        //})
        val donebtn = findViewById(R.id.donebutton) as Button
        donebtn.setOnClickListener {
            var duration: Int
            val toggle = findViewById(R.id.toggleToast) as ToggleButton
            if (toggle.isEnabled == true) {
                val durationinput = findViewById(R.id.ToastDuration) as EditText
                duration = durationinput.text.toString().toInt()
                SMSPreviewWidget.toastduration = duration
            }
            finish()
        }
    }
}