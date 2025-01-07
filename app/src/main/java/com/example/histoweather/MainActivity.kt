package com.example.histoweather

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.histoweather.data.DataModelDatabase
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDataModel
import com.example.histoweather.ui.navbar.AppNavHost
import com.example.histoweather.ui.navbar.NavigationItem
import com.example.histoweather.ui.theme.Colors
import com.example.histoweather.ui.theme.HistoWeatherTheme
import com.example.histoweather.ui.theme.ThemeMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Main Activity
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    val database = DataModelDatabase.getDatabase(applicationContext)
                    val settingsDao = database.settingsDao()
                    val favouritesDao = database.favouritesDao()
                    val searchesDao = database.searchesDao()
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(settingsDao, favouritesDao, searchesDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode
        )
    }

    /**
     * Handle the result of the permission request
     * get the current location if the permission was granted
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        when (requestCode) {
            REQUEST_CODE_LOCATION_PERMISSION -> {
                val coarseIndex =
                    permissions.indexOf(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                val fineIndex =
                    permissions.indexOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                if (grantResults[coarseIndex] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[fineIndex] == PackageManager.PERMISSION_GRANTED
                ) {
                    GlobalVariables.locationService.requestCurrentLocation()
                } else if (grantResults[coarseIndex] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Degraded user experience. Enable fine location in your device settings",
                        Toast.LENGTH_LONG
                    ).show()
                    GlobalVariables.locationService.requestCurrentLocation()
                } else {
                    // Permission denied
                    Toast.makeText(
                        this,
                        "Location permission denied. Enable it in your device settings",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    /**
     * Create the activity. Set the content to the main screen and get the current location
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalVariables.locationService = LocationService(this)
        GlobalVariables.locationService.requestCurrentLocation()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastMessage.collectLatest { message ->
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        enableEdgeToEdge()
        setContent {

            val themeModeString by viewModel.theme.collectAsState(initial = "System")
            var themeMode = ThemeMode.fromString(themeModeString) ?: ThemeMode.System
            val favouritesList by viewModel.favouriteCities.collectAsState(initial = emptyList())
            val searchedList by viewModel.searchedCities.collectAsState(initial = emptyList())

            HistoWeatherTheme(themeMode = themeMode) {
                SetColors()
                val navController = rememberNavController()
                val isMetric by viewModel.isMetric.collectAsState(initial = true)

                if (isMetric == null) {
                    CircularProgressIndicator()
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainScreen(
                            navController = navController,
                            themeMode = themeMode,
                            innerPadding = innerPadding,
                            isMetric = isMetric,
                            onMetricChange = { newIsMetric -> viewModel.updateMetric(newIsMetric) },
                            favourites = favouritesList,
                            onFavouriteToggle = { favourite, newValue ->
                                if (newValue) {
                                    viewModel.updateFavourite(listOf(favourite))
                                } else {
                                    viewModel.removeFavourite(favourite.city, favourite.countryCode, favourite.idAPI)
                                }
                            },
                            searches = searchedList,
                            onSearchesChange = { newSearches, newValues ->
                                if (newValues) {
                                    viewModel.updateSearches(listOf(newSearches))
                                } else {
                                    viewModel.removeSearch(newSearches.city, newSearches.countryCode, newSearches.idAPI)
                                }
                            },
                            onThemeChange = { theme ->
                                viewModel.updateTheme(theme.toString())
                                // just for testing
                                GlobalVariables.themeMode.value = theme
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Global Variables, can be set with GlobalVariables.'var_name'.value
 */
object GlobalVariables {
    var date = mutableStateOf(LocalDate.now())

    var primaryDate = mutableStateOf(LocalDate.now())
    var secondaryDate = mutableStateOf(LocalDate.now())

    var locationService = LocationService(null)
    var currentLocation = mutableStateOf(invalidCurrentLocation)
    var primaryLocation = mutableStateOf(invalidCurrentLocation)
    var secondaryLocation = mutableStateOf(NoLocation)

    // just for testing
    var themeMode = mutableStateOf(ThemeMode.System)
    var testing = false // set to true to enable testing
}

@Composable
fun SetColors() {
    Colors.widgetBackground = MaterialTheme.colorScheme.secondaryContainer
    Colors.widgetForeground = MaterialTheme.colorScheme.onSecondaryContainer
    Colors.widgetOutline = MaterialTheme.colorScheme.primary
    Colors.subWidgetBackground = MaterialTheme.colorScheme.secondary
    Colors.subWidgetBackgroundVariant = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
    Colors.subWidgetForeground = MaterialTheme.colorScheme.onSecondary
    Colors.subWidgetOutline = MaterialTheme.colorScheme.primary
}


/**
 * Main Screen of the app
 * @param navController navigate to any page with navController.navigate(NavigationItem.'Page_name'.route)
 * source: https://developer.android.com/jetpack/compose/navigation date: 2024-12-25
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    themeMode: ThemeMode,
    innerPadding: PaddingValues,
    isMetric: Boolean?,
    onMetricChange: (Boolean) -> Unit,
    favourites: List<FavouritesDataModel>,
    onFavouriteToggle: (FavouritesDataModel, Boolean) -> Unit,
    searches: List<SearchesDataModel>,
    onSearchesChange: (SearchesDataModel, Boolean) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
) {
    if(isMetric == null) {
        CircularProgressIndicator()
        return
    }
    // remember the selected item
    var selectedItem by remember { mutableIntStateOf(0) }
    // remember if the back button was pressed once
    var backPressedOnce by remember { mutableStateOf(false) }

    // list of items in the navigation bar
    val items = listOf(
        NavigationItem.Home.route,
        NavigationItem.Historical.route,
        NavigationItem.Compare.route,
        NavigationItem.Settings.route
    )

    // list of icons for the navigation bar
    val selectedIcons = listOf(
        Icons.Default.Place,
        Icons.Default.Today,
        Icons.AutoMirrored.Default.CompareArrows,
        Icons.Default.Settings
    )

    BackHandler {
        if (backPressedOnce) {
            // Close the app
            (navController.context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(navController.context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            // Set backPressedOnce to false after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce = false }, 2000)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                selectedIcons[index],
                                contentDescription = item
                            )
                        },
                        label = { Text(item) },
                        selected = currentDestination?.hierarchy?.any { it.route == item } == true,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id)
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onThemeChange = onThemeChange,
            onNavBarItemSelected = { selectedItem = it },
            themeMode = themeMode,
            isMetric = isMetric,
            onMetricChange = onMetricChange,
            favourites = favourites,
            onFavouriteToggle = onFavouriteToggle,
            searches = searches,
            onSearchesChange = onSearchesChange
        )
    }
}