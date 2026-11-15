package com.personalproject.mydiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.personalproject.mydiary.data.dao.FolderDao
import com.personalproject.mydiary.data.dao.ProfileDao
import com.personalproject.mydiary.data.local.entity.EntryEntity
import com.personalproject.mydiary.data.local.entity.FolderEntity
import com.personalproject.mydiary.data.local.entity.ProfileEntity

@Database(
    entities = [FolderEntity::class, ProfileEntity::class, EntryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun profileDao(): ProfileDao
}
