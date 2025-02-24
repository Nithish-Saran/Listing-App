package com.listingapp.viewstate

/**
 * Represents different states for the AppBar.
 * This includes variations for the left side (title, back button) and right side (weather info, none).
 */
sealed class AppBarState {

    //Defines the left-side variations of the AppBar.
    sealed class Left : AppBarState() {
        data class BackWith1Title(val title: String) :
            Left()   // AppBar with only a title, with a back button

        data class TitleOnly(val title: String) :
            Left()    // AppBar with only a title, without a back button
    }

    //Defines the right-side variations of the AppBar.
    sealed class Right : AppBarState() {
        /** Represents an AppBar without any right-side content */
        data object None : Right()

        /** Represents an AppBar with local weather details */
        data class Weather(
            val degree: Int?,   // Temperature in degrees
            val city: String?,  // City name
            val status: String?, // Weather status (e.g., "Cloudy", "Rainy")
            val image: String?   // URL or resource identifier for weather icon
        ) : Right()
    }
}

/**
 * Represents the complete state of the AppBar, combining both left and right content.
 */
data class AppBarViewState(
    val left: AppBarState.Left,
    val right: AppBarState.Right,
) {
    companion object {

        // Creates an `AppBarViewState` with only a title.
        fun getTitle(title: String): AppBarViewState = AppBarViewState(
            left = AppBarState.Left.TitleOnly(title),
            right = AppBarState.Right.None
        )

        //Creates an `AppBarViewState` with a title and local weather details.
        fun getLocalWeather(
            title: String,
            degree: Int,
            city: String,
            status: String,
            image: String
        ): AppBarViewState =
            AppBarViewState(
                left = AppBarState.Left.TitleOnly(title),
                right = AppBarState.Right.Weather(degree, city, status, image)
            )

        //Creates an `AppBarViewState` with a title and a back button.
        fun getTitleWithBack(title: String): AppBarViewState = AppBarViewState(
            left = AppBarState.Left.BackWith1Title(title),
            right = AppBarState.Right.None
        )
    }
}

//Represents different states related to user data.
sealed class UserDataState {

    /** Represents various states of the user list. */
    sealed class UserListState : UserDataState() {
        /** State representing a loading process */
        data object Loading : UserListState()

        /** State representing a lack of internet connection */
        data object NoNetwork : UserListState()

        /** State representing an empty user list */
        data object NoData : UserListState()

        /** State representing a successful data retrieval */
        data object Success : UserListState()
    }
}
