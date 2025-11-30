package com.example.boxtrakr.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBox(box: BoxEntity)

    @Query("SELECT * FROM boxes")
    fun getAll(): Flow<List<BoxEntity>>

    @Insert
    suspend fun insert(box: BoxEntity)

    @Query("SELECT * FROM boxes WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): BoxEntity?
}
