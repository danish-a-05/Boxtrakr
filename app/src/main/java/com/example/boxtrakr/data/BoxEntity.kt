package com.example.boxtrakr.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boxes")
data class BoxEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryName: String,
    // variables for password locked box
    val isPrivate: Boolean = false,
    val password: String? = null,
    // persistent path to the thumbnail image file (stored in internal storage)
    val imagePath: String? = null
)
