package com.example.boxtrakr.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxContentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBoxContent(content: BoxContentEntity)

    @Query("SELECT * FROM box_contents")
    fun getAll(): Flow<List<BoxContentEntity>>

    @Insert
    suspend fun insert(boxContent: BoxContentEntity)
}
