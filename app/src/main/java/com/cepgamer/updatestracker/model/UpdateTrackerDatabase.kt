package com.cepgamer.updatestracker.model

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant

@Database(entities = [RawHtmlEntity::class], version = 2,
    autoMigrations = [AutoMigration (from = 1, to = 2)])
@TypeConverters(UpdateTrackerDatabase.InstantConverter::class)
abstract class UpdateTrackerDatabase : RoomDatabase() {
    abstract fun htmlDao(): RawHtmlDao

    companion object {
        private var INSTANCE: UpdateTrackerDatabase? = null

        fun getDatabase(context: Context): UpdateTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UpdateTrackerDatabase::class.java,
                    "update_tracker_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }

    }

    object InstantConverter {
        @TypeConverter
        fun fromMillis(value: Long): Instant {
            return Instant.ofEpochMilli(value)
        }

        @TypeConverter
        fun toMillis(value: Instant): Long {
            return value.toEpochMilli()
        }
    }
}
