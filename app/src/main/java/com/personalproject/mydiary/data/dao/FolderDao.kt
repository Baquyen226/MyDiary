package com.personalproject.mydiary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.personalproject.mydiary.data.local.entity.FolderEntity

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addFolder(folder: FolderEntity)

    // TODO: Check on this again when dealing with items in the folder later
    @Delete
    suspend fun removeFolder(folder: FolderEntity)

    @Update
    suspend fun updateFolder(folder: FolderEntity)
}