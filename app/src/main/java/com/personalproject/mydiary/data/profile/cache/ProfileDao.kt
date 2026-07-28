package com.personalproject.mydiary.data.profile.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addProfile(profile: ProfileEntity)

    @Delete
    suspend fun removeProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)
}