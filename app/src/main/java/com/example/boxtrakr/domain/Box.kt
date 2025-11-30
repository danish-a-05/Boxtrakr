package com.example.boxtrakr.domain

data class Box(
    val name: String,
    val contents: MutableList<BoxContent> = mutableListOf(),
    val isPrivate: Boolean = false,
    val password: String? = null
)
