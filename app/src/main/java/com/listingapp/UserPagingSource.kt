package com.listingapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.listingapp.db.dao.UserDao
import com.listingapp.db.entity.UserEntity

class UserPagingSource(
    private val userDao: UserDao // DAO for fetching users
) : PagingSource<Int, UserEntity>() { // Make sure UserEntity is correct

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserEntity> {
        return try {
            val page = params.key ?: 0
            val users = userDao.getUsers(page * 25, 25)

            val nextKey = if (users.size < 25) null else page + 1
            val prevKey = if (page == 0) null else page - 1

            LoadResult.Page(
                data = users,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
