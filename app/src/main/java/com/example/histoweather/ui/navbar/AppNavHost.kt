package com.example.histoweather.ui.navbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDataModel
import com.example.histoweather.ui.pages.Compare
import com.example.histoweather.ui.pages.Historical
import com.example.histoweather.ui.pages.Home
import com.example.histoweather.ui.pages.Location
import com.example.histoweather.ui.pages.Settings
import com.example.histoweather.ui.theme.ThemeMode


/**
 * AppNavHost is a composable that defines the navigation routes for the app.
 * It uses the NavigationItem enum to define the routes and the corresponding composable functions.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavigationItem.Home.route,
    onThemeChange: (ThemeMode) -> Unit,
    onNavBarItemSelected: (Int) -> Unit,
    themeMode: ThemeMode,
    isMetric: Boolean,
    onMetricChange: (Boolean) -> Unit,
    favourites: List<FavouritesDataModel>,
    onFavouriteToggle: (FavouritesDataModel, Boolean) -> Unit,
    searches: List<SearchesDataModel>,
    onSearchesChange: (SearchesDataModel, Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,

    ) {
        composable(NavigationItem.Home.route) {
            Home(navController, onNavBarItemSelected, isMetric)
        }
        composable(NavigationItem.Historical.route) {
            Historical(navController, onNavBarItemSelected, isMetric)
        }
        composable(NavigationItem.Compare.route) {
            Compare(navController, onNavBarItemSelected, isMetric)
        }
        composable(NavigationItem.Settings.route) {
            // onThemeChange is a callback function that is called when the theme is changed
            // themeMode is the current ThemeMode: Light, Dark, System
            Settings(onThemeChange = onThemeChange, themeMode = themeMode, isMetric, onMetricChange) // isMetric is true by default
        }
        composable(
            // origin is the origin location form which the Location page is called (default is Home)
            // setSecondary true if it's the second location in the Compare page (default is false)
            route = NavigationItem.Location.route,
            arguments = listOf(
                navArgument("origin") { type = NavType.StringType },
                navArgument("setSecondary") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val origin = backStackEntry.arguments?.getString("origin") ?: NavigationItem.Home.route
            val setSecondary = backStackEntry.arguments?.getBoolean("setSecondary") ?: false
            Location(
                navController,
                origin,
                setSecondary,
                favourites,
                onFavouriteToggle,
                searches,
                onSearchesChange
            )
        }
    }
}