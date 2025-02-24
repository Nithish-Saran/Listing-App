package com.listingapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.listingapp.db.dao.UserDao
import com.listingapp.db.entity.UserEntity

/**
 * Room database for storing user data.
 */
@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    /**
     * Provides access to UserDao for database operations.
     */
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        /**
         * Returns a singleton instance of the database.
         * Ensures only one instance of the database is created throughout the app.
         */
        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

