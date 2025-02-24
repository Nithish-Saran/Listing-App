package com.listingapp.repo

import com.listingapp.db.dao.UserDao
import com.listingapp.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository class for managing user data operations.
 * This acts as an abstraction layer between the ViewModel and the database (DAO).
 *
 * @param userDao The DAO interface for accessing user-related database operations.
 */
class UserRepository @Inject constructor(private val userDao: UserDao) {

    /**
     * Inserts or updates a user in the database.
     *
     * @param user The user entity to be inserted or updated.
     */
    suspend fun insertUser(user: UserEntity) {
        userDao.insertOrUpdate(user)
    }

    /**
     * Searches for users whose attributes match the given query.
     *
     * @param query The search term to filter users.
     * @return A Flow that emits a list of matching users.
     */
    fun searchUsers(query: String): Flow<List<UserEntity>> {
        return userDao.searchUsers(query)
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param userId The unique identifier of the user.
     * @return A Flow that emits the user entity if found.
     */
    fun getUserById(userId: String): Flow<UserEntity> {
        return userDao.getUserById(userId)
    }

    /**
     * Retrieves the total count of users stored in the database.
     *
     * @return The total number of users as an Integer.
     */
    suspend fun getTotalUserCount(): Int {
        return userDao.getTotalUserCount()
    }

    /**
     * Fetches a paginated list of users from the database.
     *
     * @param offset The starting position for fetching users.
     * @param limit The maximum number of users to retrieve.
     * @return A list of user entities within the specified range.
     */
    suspend fun getUsers(offset: Int, limit: Int): List<UserEntity> {
        return userDao.getUsers(offset, limit)
    }
}

