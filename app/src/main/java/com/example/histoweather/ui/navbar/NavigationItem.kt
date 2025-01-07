package com.example.histoweather.ui.navbar

sealed class NavigationItem(val route: String) {
    /**
     * Navigation items for the bottom navigation bar
     */
    object Home : NavigationItem("Home")
    object Historical : NavigationItem("Historical")
    object Compare : NavigationItem("Compare")
    object Settings : NavigationItem("Settings")
    object Location : NavigationItem("location/{origin}/{setSecondary}") {
        // origin is the origin location form which the Location page is called (default is Home)
        // setSecondary true if it's the second location in the Compare page (default is false)
        fun createRoute(origin: String, setSecondary: Boolean) = "location/$origin/$setSecondary"
    }
}