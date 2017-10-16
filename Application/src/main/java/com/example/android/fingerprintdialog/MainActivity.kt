package com.example.android.fingerprintdialog

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.widget.Button

/**
 * Created by wangx on 2017/10/15.
 */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ssssssss)
        val purchase_button = findViewById<Button>(R.id.purchase_button)
        purchase_button.setOnClickListener {
            var fragment: Fragment;
            fragment = FingerprintFragment()
            val fm = fragmentManager
            val transaction = fm.beginTransaction()
            transaction?.replace(R.id.id_content, fragment)
            transaction?.commit()
        }
    }
}