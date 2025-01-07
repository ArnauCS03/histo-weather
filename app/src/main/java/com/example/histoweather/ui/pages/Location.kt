package com.example.histoweather.ui.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.histoweather.GlobalVariables
import com.example.histoweather.R
import com.example.histoweather.api.geocoding.GeocodingClient
import com.example.histoweather.api.geocoding.OpenMeteoGeocodingClient
import com.example.histoweather.api.geocoding.Place
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDataModel
import com.example.histoweather.test.MockGeocoding
import com.example.histoweather.ui.rearrangeResults

/**
 * Page with SearchBar and saved Locations
 * @param navController navigation controller
 * @param origin the page that called Location
 * @param setSecondary to differentiate between the two locations in Compare.
 * @param favourites list of favourite locations
 * @param onFavouriteToggle function to detect changes in favourite location
 * @param searches list of recent searches
 * @param onSearchesChange function to detect changes in recent searches
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Location(
    navController: NavController,
    origin: String,
    setSecondary: Boolean,
    favourites: List<FavouritesDataModel>,
    onFavouriteToggle: (FavouritesDataModel, Boolean) -> Unit,
    searches: List<SearchesDataModel>,
    onSearchesChange: (SearchesDataModel, Boolean) -> Unit
) {
    val omgc: GeocodingClient = if (GlobalVariables.testing) MockGeocoding() else OpenMeteoGeocodingClient()
    var results: List<Place> by remember { mutableStateOf(listOf()) }
    val textFieldState = rememberTextFieldState()
    var isInteracting by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxWidth()
            .testTag("location_page")
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f }
                .testTag("search_bar"),
            inputField = {
                SearchBarDefaults.InputField(
                    state = textFieldState,
                    onSearch = {
                        try {
                            // if user presses search button, we select the first entry
                            val result = results[0]

                            // set the location
                            if (!setSecondary) GlobalVariables.primaryLocation.value = result
                            else GlobalVariables.secondaryLocation.value = result

                            // jump back to the previous page
                            navController.popBackStack()
                        } catch (e: Exception) {
                            println("Unexpected error: ${e.message}")
                        }
                    },
                    expanded = true,
                    onExpandedChange = { isInteracting = true },
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = {
                        IconButton(onClick = {
                            // jump back to the previous page
                            navController.popBackStack()
                        }, Modifier.testTag("location_backButton")) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            textFieldState.clearText()
                            Modifier.testTag("search_bar_clear")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "search_bar_clear")
                        }
                    },
                    modifier = Modifier.testTag("search_bar_input")
                )
            },
            expanded = true,
            onExpandedChange = {},
            windowInsets = WindowInsets(top = 0.dp),
        ) {
            // get results
            LaunchedEffect(textFieldState.text) {
                results = try {
                    omgc.getByName(textFieldState.text.toString()).results
                } catch (_: Exception) {
                    listOf()
                }
            }


            // content
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // Current Location
                ListItem(
                    headlineContent = { Text(stringResource(R.string.current_location_full)) },
                    leadingContent = {
                        Icon(Icons.Default.MyLocation, contentDescription = null)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier =
                    Modifier
                        .clickable {
                            GlobalVariables.locationService.requestCurrentLocation()
                            if (!setSecondary) GlobalVariables.primaryLocation.value =
                                GlobalVariables.currentLocation.value
                            else GlobalVariables.secondaryLocation.value =
                                GlobalVariables.currentLocation.value

                            // jump back to the previous page
                            navController.popBackStack()
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("location_currentLocation")
                )


                // not clicking yet the search bar. Show Favourites only
                if (!isInteracting) {

                    favourites.forEach { fav ->
                        ListElement(
                            place = Place(
                                fav.idAPI,
                                fav.city,
                                0.0,
                                0.0,
                                null,
                                null,
                                fav.countryCode,
                            ),
                            omgc = omgc,
                            navController = navController,
                            origin = origin,
                            setSecondary = setSecondary,
                            isFavourite = true,
                            onFavouriteToggle = { onFavouriteToggle(fav, false) },
                            favourites = favourites,
                            searches = searches,
                            onSearchesChange = { },
                            isInteracting = isInteracting,
                            showRecentSearches = false,
                            onDeleteSearch = { _, _, _ -> },
                            onDeleteFavouriteLongPress = { city, countryCode, idAPI ->
                                onFavouriteToggle(FavouritesDataModel(0, idAPI, city, countryCode), false)
                            },
                        )
                    }
                } else {

                    // Show recent searches, when clicking in the search bar and before writing text
                    if (textFieldState.text.isEmpty()) {

                        searches.asReversed().forEach { srch ->
                            ListElement(
                                place = Place(
                                    srch.idAPI,
                                    srch.city,
                                    0.0,
                                    0.0,
                                    null,
                                    null,
                                    srch.countryCode
                                ),
                                omgc = omgc,
                                navController = navController,
                                origin = origin,
                                setSecondary = setSecondary,
                                isFavourite = favourites.any { it.idAPI == srch.idAPI },
                                onFavouriteToggle = { isAdding ->
                                    val updatedFavourites = if (isAdding) {
                                        favourites + FavouritesDataModel(
                                            0,
                                            srch.idAPI,
                                            srch.city,
                                            srch.countryCode.toString()
                                        )
                                    } else {
                                        favourites.filter { it.city != srch.city || it.countryCode != srch.countryCode }
                                    }
                                    onFavouriteToggle(
                                        FavouritesDataModel(
                                            0,
                                            srch.idAPI,
                                            srch.city,
                                            srch.countryCode.toString()
                                        ), isAdding
                                    )
                                },
                                favourites = favourites,
                                searches = searches,
                                onSearchesChange = { isAdding ->
                                    val updatedSearches = if (isAdding) {
                                        searches + SearchesDataModel(
                                            0,
                                            srch.idAPI,
                                            srch.city,
                                            srch.countryCode
                                        )
                                    } else {
                                        searches.filter { it.id != srch.idAPI }
                                    }
                                    onSearchesChange(
                                        SearchesDataModel(
                                            0,
                                            srch.idAPI,
                                            srch.city,
                                            srch.countryCode
                                        ), isAdding
                                    )
                                },
                                isInteracting = isInteracting,
                                showRecentSearches = textFieldState.text.isEmpty(),
                                onDeleteSearch = { city, countryCode, idAPI ->
                                    onSearchesChange(SearchesDataModel(0, idAPI, city, countryCode), false)
                                },
                                onDeleteFavouriteLongPress = { _, _, _ -> },
                            )
                        }
                    }

                    // Print results

                    if (results.isNotEmpty()) {

                        // filter to remove not matching searched city name string (textFieldState.text)
                        val filteredResults = results.filter { place ->
                            place.name.startsWith(textFieldState.text, ignoreCase = true)
                        }

                        // Rearrange the filtered results
                        results = rearrangeResults(favourites, searches, filteredResults)

                        results.forEach { place ->
                            ListElement(
                                place = place,
                                omgc = omgc,
                                navController = navController,
                                origin = origin,
                                setSecondary = setSecondary,
                                isFavourite = favourites.any { it.idAPI == place.id },
                                onFavouriteToggle = { isAdding ->
                                    val updatedFavourites = if (isAdding) {
                                        favourites + FavouritesDataModel(
                                            0,
                                            place.id,
                                            place.name,
                                            place.countryCode.toString()
                                        )
                                    } else {
                                        favourites.filter { it.city != place.name || it.countryCode != place.countryCode }
                                    }
                                    onFavouriteToggle(
                                        FavouritesDataModel(
                                            0,
                                            place.id,
                                            place.name,
                                            place.countryCode.toString()
                                        ), isAdding
                                    )
                                },
                                favourites = favourites,
                                searches = searches,
                                onSearchesChange = { isAdding ->
                                    val updatedSearches = if (isAdding) {
                                        searches + SearchesDataModel(
                                            0,
                                            place.id,
                                            place.name,
                                            place.countryCode.toString()
                                        )
                                    } else {
                                        searches.filter { it.city != place.name || it.countryCode != place.countryCode }
                                    }
                                    onSearchesChange(
                                        SearchesDataModel(
                                            0,
                                            place.id,
                                            place.name,
                                            place.countryCode.toString()
                                        ), isAdding
                                    )
                                },
                                isInteracting = isInteracting,
                                showRecentSearches = false,
                                onDeleteSearch = { city, countryCode, idAPI ->
                                    onSearchesChange(SearchesDataModel(0, idAPI, city, countryCode), false)
                                },
                                onDeleteFavouriteLongPress = { _, _, _ -> },
                            )
                        }
                    }
                }
            }
        }

        // Handle back gesture
        BackHandler {
            // jump back to the previous page
            navController.popBackStack()
        }
    }
}

/**
 * Clickable list element for the search results
 * Automatically sets icons based on the location's status in history and favorites
 * @param place location
 * @param omgc OpenMeteoGeocodingClient
 * @param navController navigation controller
 * @param origin the page that called Location
 * @param setSecondary to differentiate between the two locations in Compare.
 * @param isFavourite boolean indicating if a location is added to favourites
 * @param onFavouriteToggle function to detect changes in favourite
 * @param searches list of recent searches
 * @param onSearchesChange function to detect changes in recent searches
 * @param isInteracting boolean indicating if the user is interacting with the search bar
 * @param showRecentSearches boolean indicating if the recent searches should be shown
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListElement(
    place: Place,
    omgc: GeocodingClient,
    navController: NavController,
    origin: String,
    setSecondary: Boolean,
    isFavourite: Boolean,
    onFavouriteToggle: (Boolean) -> Unit,
    favourites: List<FavouritesDataModel>,
    searches: List<SearchesDataModel>,
    onSearchesChange: (Boolean) -> Unit,
    isInteracting: Boolean,
    showRecentSearches: Boolean,
    onDeleteSearch: (String, String, Int) -> Unit,
    onDeleteFavouriteLongPress: (String, String, Int) -> Unit,
) {

    // variable for storing the fetched favourite data, if the favourite city is clicked
    var showDialog by remember { mutableStateOf(false) }

    var favouritesInfo by remember { mutableStateOf<Place?>(null) }
    var searchesInfo by remember { mutableStateOf<Place?>(null) }



    // only fetch data from the api if we need it again for the favourite city
    if (isFavourite && !isInteracting) {
        // Fetch favorites info when the user interacts
        LaunchedEffect(isFavourite) {
            favouritesInfo = try {
                omgc.getById(place.id)
            } catch (_: Exception) {

                null
            }
        }
    }
    // only fetch data from the api if we need it again for the recent search
    else if (isInteracting && showRecentSearches) {
        LaunchedEffect(isInteracting, showRecentSearches) {
            searchesInfo = try {
                omgc.getById(place.id)
            } catch (e: Exception) {
                null
            }
        }
    }


    ListItem(
        headlineContent = { Text(place.name) },
        supportingContent = {
            Text(
                place.countryCode.toString()
            )
        },
        leadingContent = {

            // Display the History in the left icon only when you are displaying only recent searches or displaying the result
            if (isInteracting and searches.any { it.city == place.name && it.countryCode == place.countryCode && it.idAPI == place.id } ) {
                Icon(Icons.Default.History, contentDescription = "location_history_${place.name}_${place.countryCode.toString()}")
            }
            else {
                Icon(Icons.Default.Search, contentDescription = "location_search_${place.name}_${place.countryCode.toString()}")
            }
        },
        trailingContent = {
            IconButton(onClick = { onFavouriteToggle(!isFavourite) }) {

                // Show heart filled or not

                // case of favourites tab (fill)
                if (isFavourite && !isInteracting) {
                    Icon(Icons.Filled.Favorite, contentDescription = "location_favorite_${place.name}_${place.countryCode.toString()}")
                }
                // case recent searches tab (fill)
                else if (isFavourite && showRecentSearches && favourites.any {it.idAPI == place.id}) {
                    Icon(Icons.Filled.Favorite, contentDescription = "location_favorite_${place.name}_${place.countryCode.toString()}")
                }
                // case results tab (fill)
                else if (isFavourite && favourites.any {it.idAPI == place.id}) {
                    Icon(Icons.Filled.Favorite, contentDescription = "location_favorite_${place.name}_${place.countryCode.toString()}")
                }
                // Not fill
                else Icon(Icons.Outlined.FavoriteBorder, contentDescription = "location_noFavorite_${place.name}_${place.countryCode.toString()}")
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier =
        Modifier
            .combinedClickable(
                onClick = {

                    // update the location in the case of favourite getting clicked
                    if (isFavourite && !isInteracting) {
                        if (favouritesInfo != null) {
                            if (!setSecondary) GlobalVariables.primaryLocation.value =
                                favouritesInfo!!
                            else GlobalVariables.secondaryLocation.value = favouritesInfo!!
                        }
                    }
                    // update location in case of recent search getting clicked
                    else if (isInteracting && showRecentSearches) {
                        if (searchesInfo != null) {
                            if (!setSecondary) GlobalVariables.primaryLocation.value = searchesInfo!!
                            else GlobalVariables.secondaryLocation.value = searchesInfo!!
                        }
                    }
                    else { // update the location using the place info
                        if (!setSecondary) GlobalVariables.primaryLocation.value = place
                        else GlobalVariables.secondaryLocation.value = place
                    }

                    onSearchesChange(true)

                    // jump back to the previous page
                    navController.popBackStack()
                },
                onLongClick = {
                    if (searches.any{ it.city == place.name && it.countryCode == place.countryCode } || isFavourite) {
                        showDialog = true
                    }
                }

            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("location_listElement_${place.name}_${place.countryCode.toString()}")
    )
    // Show the delete dialog when deleting a recent search
    if (showDialog) {

        // Logic for long press when favourite
        if (!isInteracting) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete Favourite") },
                text = { Text("Are you sure you want to delete this favourite?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteFavouriteLongPress(place.name, place.countryCode.toString(), place.id)
                            showDialog = false
                        }
                    ) {
                        Text("Yes", color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("delete_favourite_dialog_ok"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                },
                modifier = Modifier.testTag("delete_favourite_dialog")
            )
        } else if (showRecentSearches) {  // show this dialog if we long pressed on recent search
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete Search") },
                text = { Text("Are you sure you want to delete this search?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteSearch(place.name, place.countryCode.toString(), place.id)
                            showDialog = false
                        }
                    ) {
                        Text("Yes", color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("delete_history_dialog_ok"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                },
                modifier = Modifier.testTag("delete_history_dialog")
            )
        }
    }
}