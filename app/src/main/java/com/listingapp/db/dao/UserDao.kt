package com.listingapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.listingapp.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

//DAO for managing user data in the database.
@Dao
interface UserDao {

    //Searches users by their full name.
    @Query("""
        SELECT * FROM user_table 
        WHERE (firstName || ' ' || lastName) LIKE '%' || :query || '%'
    """)
    fun searchUsers(query: String): Flow<List<UserEntity>>

    //Gets a list of users with pagination.
    @Query("SELECT * FROM user_table ORDER BY uuid ASC LIMIT :limit OFFSET :offset")
    suspend fun getUsers(offset: Int, limit: Int): List<UserEntity>

    //Returns the total number of users.
    @Query("SELECT COUNT(*) FROM user_table")
    suspend fun getTotalUserCount(): Int

    //Checks if a user exists by their ID.
    @Query("SELECT COUNT(*) FROM user_table WHERE uuid = :userId")
    suspend fun isUserExists(userId: String): Int

    //Inserts a user, ignoring duplicates.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity)

    //Updates an existing user.
    @Update
    suspend fun updateUser(user: UserEntity)

    //Inserts or updates a user based on their ID.
    suspend fun insertOrUpdate(user: UserEntity) {
        if (isUserExists(user.uuid) > 0) {
            updateUser(user)
        } else {
            insertUser(user)
        }
    }

    // Gets a user by their ID.
    @Query("SELECT * FROM user_table WHERE uuid = :userId")
    fun getUserById(userId: String): Flow<UserEntity>

    //Gets all users from the database.
    @Query("SELECT * FROM user_table")
    fun getAllUsers(): List<UserEntity>
}


