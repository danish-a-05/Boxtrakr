package com.example.boxtrakr.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "box_contents")
data class BoxContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: Int,
    val boxName: String
)
