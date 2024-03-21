package com.cepgamer.updatestracker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cepgamer.updatestracker.model.RawHtmlDao
import com.cepgamer.updatestracker.model.RawHtmlEntity
import kotlinx.coroutines.launch

class RawHtmlViewModel(private val rawHtmlDao: RawHtmlDao) : ViewModel() {

    val rawHtmlFlow = rawHtmlDao.getAllAsFlow()

    fun insert(rawHtmlEntity: RawHtmlEntity) =
        viewModelScope.launch { rawHtmlDao.upsertHtmls(rawHtmlEntity) }
}

class RawHtmlVMFactory(private val rawHtmlDao: RawHtmlDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RawHtmlViewModel(rawHtmlDao) as T
    }
}
