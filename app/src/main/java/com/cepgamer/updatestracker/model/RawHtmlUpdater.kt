package com.cepgamer.updatestracker.model

import android.content.Context
import android.util.Log
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

        runBlocking(Dispatchers.IO) {
            rawHtmlDao.upsertHtmls(addressOnlyEntity)
            updateHtml(addressOnlyEntity)
            rawHtmlDao.upsertHtmls(addressOnlyEntity)
        }
    }

    fun deleteHtml(address: String) {
        runBlocking(Dispatchers.IO) {
            rawHtmlDao.delete(rawHtmlDao.getByHtml(address))
        }
    }

    private suspend fun updateHtml(entity: RawHtmlEntity): Pair<Boolean, RawHtmlEntity> {
        val raw = withContext(Dispatchers.IO) {
            try {
                return@withContext BufferedInputStream(
                    URL(entity.address).openConnection().getInputStream()
                ).readBytes()
                    .toString(Charsets.UTF_8)
            } catch (e: Throwable) {
                Log.i(javaClass.name, "Failed to download html.")
            }

            return@withContext ""
        }.replace("\\s".toRegex(), "")
            .replace("""(</\w+>)""".toRegex(), "\$1\n")
            .replace("""(<\w+>)""".toRegex(), "\n\$1").split("\n")
            .filter { line -> !line.contains(filteredRegex) }
            .joinToString("\n")

        if (raw.isBlank()) {
            return false to RawHtmlEntity("", "", Instant.now(), Instant.now())
        }

        val currentTime = Instant.now()
        val isUpdated = raw != entity.htmlValue
        val newEntity = RawHtmlEntity(
            entity.address,
            raw,
            currentTime,
            if (isUpdated) currentTime else entity.lastUpdate,
        )

        if (isUpdated) {
            Log.i(javaClass.name, "Diff is: ${getDiff(entity.htmlValue, raw)}")
        }

        return isUpdated to newEntity
    }

    companion object {
        val FILTERED_STRINGS = listOf(
            "href",
            "hash",
            "email",
            "token",
            "protect",
            "script",
            "nonce",
            "function",
            "data-ei",
            "jsdata",
            "style"
        )

        val filteredRegex = FILTERED_STRINGS.joinToString("|").toRegex()

        fun getDiff(a: String, b: String): String {
            val al = a.split("\n")
            val bl = b.split("\n")

            return al.zip(bl).filter { (x, y) -> x != y }
                .joinToString("\n") { (x, y) -> "-$x\n+$y" }
        }

    }
}