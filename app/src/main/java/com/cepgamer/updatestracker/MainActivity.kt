package com.cepgamer.updatestracker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cepgamer.updatestracker.model.UpdateTrackerDatabase
import com.cepgamer.updatestracker.ui.main.HtmlPageFragment
import com.cepgamer.updatestracker.ui.main.RawHtmlVMFactory
import com.cepgamer.updatestracker.ui.main.RawHtmlViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = UpdateTrackerDatabase.getDatabase(application).htmlDao()

        val vm = viewModels<RawHtmlViewModel> { RawHtmlVMFactory(dao) }
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HtmlPageFragment.newInstance(vm))
                .commitNow()
        }
    }
}