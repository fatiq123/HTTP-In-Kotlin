package com.example.httpinkotlin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MenuItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val price: String
)
