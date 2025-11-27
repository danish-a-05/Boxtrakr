package com.example.boxtrakr.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boxes")
data class BoxEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryName: String
)
