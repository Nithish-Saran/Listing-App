package com.listingapp.db

import android.app.Application
import androidx.room.Room
import com.listingapp.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing database-related dependencies.
 * This ensures that the database and DAO instances are available
 * for dependency injection throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of the Room database.
     * @param application The application context used to initialize the database.
     * @return An instance of [UserDatabase].
     */
    @Provides
    @Singleton
    fun provideDatabase(application: Application): UserDatabase {
        return Room.databaseBuilder(
            application,
            UserDatabase::class.java,
            "user_database"
        ).build()
    }

    /**
     * Provides an instance of [UserDao] from the database.
     * @param database The instance of [UserDatabase].
     * @return An instance of [UserDao] for accessing user data.
     */
    @Provides
    @Singleton
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }
}

