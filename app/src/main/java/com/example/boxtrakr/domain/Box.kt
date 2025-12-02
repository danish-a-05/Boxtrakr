package com.example.boxtrakr.domain

data class Box(
    val name: String,
    val contents: MutableList<BoxContent> = mutableListOf(),
    val isPrivate: Boolean = false,
    val password: String? = null,
    // path to stored image (internal storage). null if no image.
    val imagePath: String? = null
)
