package com.example.httpinkotlin.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuDao {

    @Query("SELECT * FROM MenuItem")
    fun getAllMenuItems(): LiveData<List<MenuItem>>

    @Insert
    fun saveMenuItems(menuItem: MenuItem)

    @Delete
    fun deleteMenuItem(menuItem: Int)
}