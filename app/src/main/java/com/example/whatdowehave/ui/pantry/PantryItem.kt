package com.example.whatdowehave.ui.pantry

data class PantryItem(
    val name: String,
    val quantity: Int,
    val lowThreshold: Int = 1
)

