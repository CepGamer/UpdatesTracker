package com.cepgamer.updatestracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RawHtmlDao {
    @Query("Select * from RawHtmlEntity")
    fun getAll(): List<RawHtmlEntity>

    @Query("Select * from RawHtmlEntity")
    fun getAllAsFlow(): Flow<List<RawHtmlEntity>>

    @Query("Select * from RawHtmlEntity where address = :address")
    fun getByHtml(address: String): RawHtmlEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertHtmls(vararg html: RawHtmlEntity)

    @Delete
    fun delete(html: RawHtmlEntity)
}