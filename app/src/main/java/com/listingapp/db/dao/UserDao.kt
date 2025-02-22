package com.listingapp.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.listingapp.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

//@Dao
//interface UserDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUser(user: UserEntity)
//
//    @Query("SELECT * FROM user_table")
//    fun getAllUsers(): Flow<List<UserEntity>>
//
//    @Delete
//    suspend fun deleteUser(user: UserEntity)
//}

@Dao
interface UserDao {

    @Query("""
        SELECT * FROM user_table 
        WHERE (firstName || ' ' || lastName) LIKE '%' || :query || '%'
    """)
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM user_table LIMIT :limit OFFSET :offset")
    suspend fun getUsers(offset: Int, limit: Int): List<UserEntity>

    @Query("SELECT COUNT(*) FROM user_table")
    suspend fun getTotalUserCount(): Int

    @Query("SELECT COUNT(*) FROM user_table WHERE uuid = :userId")
    suspend fun isUserExists(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore duplicate inserts
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    suspend fun insertOrUpdate(user: UserEntity) {
        if (isUserExists(user.uuid) > 0) {
            updateUser(user)
        } else {
            insertUser(user)
        }
    }
    @Query("SELECT * FROM user_table WHERE uuid = :userId")
    fun getUserById(userId: String): Flow<UserEntity>

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): List<UserEntity>
}

