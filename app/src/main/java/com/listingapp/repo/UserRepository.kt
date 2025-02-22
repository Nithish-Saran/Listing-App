package com.listingapp.repo

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.listingapp.UserPagingSource
import com.listingapp.db.dao.UserDao
import com.listingapp.db.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun insertUser(user: UserEntity) {
        userDao.insertOrUpdate(user)
    }

    suspend fun allUsers() =  withContext(Dispatchers.IO) {
        userDao.getAllUsers()
    }

    fun searchUsers(query: String): Flow<List<UserEntity>> {
        return userDao.searchUsers(query)
    }

    fun getUserById(userId: String): Flow<UserEntity> {
        return userDao.getUserById(userId)
    }

    suspend fun getTotalUserCount(): Int {
        return userDao.getTotalUserCount()
    }


    fun getUsersPaged(): PagingSource<Int, UserEntity> {
        return UserPagingSource(userDao)
    }

    suspend fun getUsers(offset: Int, limit: Int): List<UserEntity> {
        return userDao.getUsers(offset, limit)
    }
}
