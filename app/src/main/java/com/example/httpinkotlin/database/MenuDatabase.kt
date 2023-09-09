package com.example.httpinkotlin.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MenuItem::class], version = 1, exportSchema = false)
abstract class MenuDatabase: RoomDatabase() {
    abstract fun menuDao(): MenuDao
}