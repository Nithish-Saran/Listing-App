package com.listingapp

import com.listingapp.db.entity.UserEntity

sealed class AppBarState {
    sealed class Left : AppBarState() {
        data class BackWith1Title (val title: String) : Left()
        data class TitleOnly (val title: String) : Left()
    }

    sealed class Right : AppBarState() {
        data object None : Right()
        data class Weather (
            val degree: Int?,
            val city: String?,
            val status: String?,
            val image: String?
        ): Right()
    }
}

data class AppBarViewState(
    val left: AppBarState.Left,
    val right: AppBarState.Right,
) {
    companion object {
        fun getTitle(title: String): AppBarViewState = AppBarViewState(
            left = AppBarState.Left.TitleOnly(title),
            right = AppBarState.Right.None
        )
        fun getWeather(title: String, degree: Int, city: String, status: String, image: String): AppBarViewState = AppBarViewState(
            left = AppBarState.Left.TitleOnly(title),
            right = AppBarState.Right.Weather(degree, city, status, image)
        )
        fun getTitileWithBack(title: String): AppBarViewState = AppBarViewState(
            left = AppBarState.Left.BackWith1Title(title),
            right = AppBarState.Right.None
        )
    }
}
 sealed class UserDataState {
     sealed class UserListState : UserDataState() {
         data object Loading : UserListState()
         data object Error : UserListState()
         data object NoData : UserListState()
         data class Success(val data: Array<UserEntity>) : UserListState()
     }
 }