package com.app.tasty.dataSource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.tasty.model.CartItem
import com.app.tasty.model.FoodItem

@Database(entities = [FoodItem::class, CartItem::class], version = 1)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDAO

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(context: Context): FoodDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext, FoodDatabase::class.java, "foodDB"
                ).build()
            }
            return INSTANCE!!
        }
    }
}