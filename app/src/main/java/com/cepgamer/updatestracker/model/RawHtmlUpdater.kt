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
        val results = rawHtmlDao.getAll().mapNotNull { updateHtml(it) }

        results.forEach(rawHtmlDao::upsertHtmls)

        return results
    }

    fun insertHtmlByAddress(address: String) {
        val currentTime = Instant.now()
        val addressOnlyEntity = RawHtmlEntity(address, "", currentTime, currentTime, address, -1)

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

    private suspend fun updateHtml(entity: RawHtmlEntity): RawHtmlEntity? {
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
            .replace("""(<\w+>)""".toRegex(), "\n\$1")
            .split("\n")
            .filter { line -> !line.contains(filteredRegex) }
            .filter { line -> !line.matches("</?\\w+>".toRegex()) }
            .filter { line -> line.isNotBlank() }
            .joinToString("\n")

        Log.d(javaClass.name, raw)

        if (raw.isBlank()) {
            return null
        }

        val currentTime = Instant.now()
        val isUpdated = raw != entity.htmlValue
        val newEntity = RawHtmlEntity(
            entity.address,
            raw,
            if (isUpdated) currentTime else entity.lastUpdate,
            currentTime,
            entity.title,
            entity.uiPosition
        )

        if (isUpdated) {
            Log.i(javaClass.name, "Updated the website")
        }

        return newEntity
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