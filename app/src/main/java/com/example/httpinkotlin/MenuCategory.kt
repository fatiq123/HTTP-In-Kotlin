package com.example.httpinkotlin

import kotlinx.serialization.Serializable

@Serializable
data class MenuCategory(
    val menu: List<String>
)
