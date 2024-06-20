package com.cepgamer.updatestracker

import android.app.NotificationManager
import android.app.UiModeManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cepgamer.updatestracker.model.UpdateTrackerDatabase
import com.cepgamer.updatestracker.ui.main.HtmlPageFragment
import com.cepgamer.updatestracker.ui.main.RawHtmlVMFactory
import com.cepgamer.updatestracker.ui.main.RawHtmlViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyDayNightMode()

        val dao = UpdateTrackerDatabase.getDatabase(application).htmlDao()

        val vm = viewModels<RawHtmlViewModel> { RawHtmlVMFactory(dao) }
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HtmlPageFragment.newInstance(vm))
                .commitNow()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        getSystemService(NotificationManager::class.java).cancelAll()
    }

    private fun applyDayNightMode() {
        val manager = getSystemService(UiModeManager::class.java)
        delegate.localNightMode = manager.nightMode
    }
}