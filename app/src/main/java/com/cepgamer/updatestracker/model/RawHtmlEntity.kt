package com.cepgamer.updatestracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class RawHtmlEntity(
    @PrimaryKey val address: String,
    @ColumnInfo val htmlValue: String,
    @ColumnInfo val lastUpdate: Instant,
    @ColumnInfo val lastCheck: Instant,
)
