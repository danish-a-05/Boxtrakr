package com.example.boxtrakr.domain

data class Category(
    val name: String,
    val boxes: MutableList<Box> = mutableListOf()
)
