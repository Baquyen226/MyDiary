package com.personalproject.mydiary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.personalproject.mydiary.data.local.entity.EntryEntity

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addEntry(entry: EntryEntity)

    @Delete
    suspend fun deleteEntry(entry: EntryEntity)

    @Update
    suspend fun updateEntry(entry: EntryEntity)
}