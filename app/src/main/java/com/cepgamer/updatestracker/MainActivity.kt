package com.cepgamer.updatestracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cepgamer.updatestracker.ui.main.HtmlPageFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HtmlPageFragment.newInstance())
                .commitNow()
        }
    }
}