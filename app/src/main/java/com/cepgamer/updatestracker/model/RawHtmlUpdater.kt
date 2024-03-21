package com.cepgamer.updatestracker.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.URL
import java.time.Instant

class RawHtmlUpdater(appContext: Context) {

    private val db = UpdateTrackerDatabase.getDatabase(appContext)
    private val rawHtmlDao = db.htmlDao()

    /** updates HTMLs and returns list of updated ones. */
    suspend fun updateHtmls(): List<RawHtmlEntity> {
        val results = rawHtmlDao.getAll().map { updateHtml(it) }

        results.map(Pair<Boolean, RawHtmlEntity>::second).forEach(rawHtmlDao::upsertHtmls)

        return results.filter(Pair<Boolean, RawHtmlEntity>::first)
            .map(Pair<Boolean, RawHtmlEntity>::second)
    }

    fun insertHtmlByAddress(address: String) {
        val currentTime = Instant.now()
        val addressOnlyEntity = RawHtmlEntity(address, "", currentTime, currentTime)

        rawHtmlDao.upsertHtmls(addressOnlyEntity)

        runBlocking(Dispatchers.IO) {
            updateHtml(addressOnlyEntity)
            rawHtmlDao.upsertHtmls(addressOnlyEntity)
        }
    }

    private suspend fun updateHtml(entity: RawHtmlEntity): Pair<Boolean, RawHtmlEntity> {
        val raw = withContext(Dispatchers.IO) {
            BufferedInputStream(URL(entity.address).openConnection().getInputStream()).readBytes()
                .toString(Charsets.UTF_8)
        }

        val currentTime = Instant.now()
        val isUpdated = raw != entity.htmlValue
        val newEntity = RawHtmlEntity(
            entity.address,
            raw,
            currentTime,
            if (isUpdated) currentTime else entity.lastUpdate,
        )

        return isUpdated to newEntity
    }
}