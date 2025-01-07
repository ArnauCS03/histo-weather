package com.example.histoweather.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.histoweather.GlobalVariables
import com.example.histoweather.api.geocoding.Place
import com.example.histoweather.api.weather.OpenMeteoClient
import com.example.histoweather.api.weather.PlaceWeatherData
import com.example.histoweather.api.weather.WeatherClient
import com.example.histoweather.test.MockClient
import com.example.histoweather.ui.DatePickerModalInput
import com.example.histoweather.ui.localDateToString
import com.example.histoweather.ui.navbar.NavigationItem
import com.example.histoweather.ui.widgets.Widget
import com.example.histoweather.R
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Status {
    CURRENT_LOCATION_INVALID,
    NO_LOCATION,
    LOADING,
    SUCCESS,
    TIMEOUT,
    ERROR,
}

enum class HeaderType {
    SINGLE,
    DOUBLE,
}


@Composable
fun SingleHeader(
    navController: NavHostController,
    onNavBarItemSelected: (Int) -> Unit,
    date: LocalDate,
    location: Place,
    source: String
) {
    // state for date picker visibility (true = show, false = hide)
    var showDatePicker by remember { mutableStateOf(false) }

    // Column for all Buttons
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
            .testTag("${source}_page")
    ) {

        // Row for the location button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Location button, shows current selected location
            Button(
                onClick = {
                    // go to location page
                    navController.navigate(
                        NavigationItem.Location.createRoute(
                            source,
                            setSecondary = false
                        )
                    ) {
                        popUpTo(source) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }, modifier = Modifier
                    .weight(1f)
                    .testTag("${source}_locationButton"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Outlined.Place,
                    contentDescription = "Place Icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    location.name,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

        }

        // Row for the date button and compare button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Date button shows current selected date
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .weight(0.85f)
                    .testTag("${source}_dateButton"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                // select date
                Icon(
                    Icons.Outlined.Today,
                    contentDescription = "Place Icon",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    localDateToString(date, true, false),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Compare Button to go to compare page
            TextButton(
                onClick = {
                    GlobalVariables.primaryLocation.value = location
                    GlobalVariables.primaryDate.value = date
                    onNavBarItemSelected(2) // Index of Compare in the Navigation Bar
                    navController.navigate(NavigationItem.Compare.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .weight(0.15f)
                    .testTag("${source}_compareButton"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.CompareArrows,
                    contentDescription = "Arrow Icon",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }

        }


        // date picker
        if (showDatePicker) {
            DatePickerModalInput(
                onDateSelected = { date ->
                    showDatePicker = false
                    if (date != null) {
                        if (date != LocalDate.now()) {
                            GlobalVariables.primaryDate.value = date
                            onNavBarItemSelected(1) // Index of Historical in the Navigation Bar
                            navController.navigate(NavigationItem.Historical.route)
                        } else {
                            onNavBarItemSelected(0) // Index of Home in the Navigation Bar
                            navController.navigate(NavigationItem.Home.route)
                        }
                    }
                },
                onDismiss = {
                    showDatePicker = false
                },
                // Provide the current date in milliseconds
                date = date
            )
        }

    }
}

@Composable
fun DoubleHeader(
    navController: NavHostController,
    locationA: Place,
    locationB: Place,
    dateA: LocalDate,
    dateB: LocalDate,
    source: String
) {
    // state for date picker visibility (true = show, false = hide)
    var showDatePicker0 by remember { mutableStateOf(false) }
    var showDatePicker1 by remember { mutableStateOf(false) }

    // store date values
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
            .testTag("compare_page")
    ) {
        // row for location pickers
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // first location button
            Button(
                onClick = {
                    navController.navigate(
                        NavigationItem.Location.createRoute(
                            source,
                            setSecondary = false
                        )
                    ) {
                        // delete all pages from the backstack until Compare
                        popUpTo(source) {
                            saveState = true
                        }
                        // no duplicates
                        launchSingleTop = true
                        // restores the state of the page
                        restoreState = true
                    }
                }, modifier = Modifier
                    .weight(1f)
                    .testTag("compare_location_button_0"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Outlined.Place,
                    contentDescription = "Place Icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(locationA.name, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onPrimary)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // second location button
            Button(
                onClick = {
                    navController.navigate(
                        NavigationItem.Location.createRoute(
                            source,
                            setSecondary = true
                        )
                    ) {
                        // delete all pages from the backstack until Compare
                        popUpTo(source) {
                            saveState = true
                        }
                        // no duplicates
                        launchSingleTop = true
                        // restores the state of the page
                        restoreState = true
                    }
                }, modifier = Modifier
                    .weight(1f)
                    .testTag("compare_location_button_1"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Outlined.Place,
                    contentDescription = "Place Icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(locationB.name, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        // row for date pickers
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // first date picker
            Button(
                onClick = { showDatePicker0 = true },
                modifier = Modifier
                    .weight(1f)
                    .testTag("compare_date_button_0"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(
                    Icons.Outlined.Today,
                    contentDescription = "Date Icon",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    dateA.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // second date picker
            Button(
                onClick = { showDatePicker1 = true },
                modifier = Modifier
                    .weight(1f)
                    .testTag("compare_date_button_1"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(
                    Icons.Outlined.Today,
                    contentDescription = "Date Icon",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    dateB.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        // date picker for first location
        if (showDatePicker0) {
            DatePickerModalInput(
                onDateSelected = { selectedDate ->
                    // store date and close date picker
                    GlobalVariables.primaryDate.value = selectedDate ?: LocalDate.now()
                    showDatePicker0 = false
                },
                onDismiss = {
                    // close date picker when cancel is pressed
                    showDatePicker0 = false
                },
                date = dateA
            )
        }

        // date picker for second location
        if (showDatePicker1) {
            DatePickerModalInput(
                onDateSelected = { selectedDate ->
                    // store date and close date picker
                    GlobalVariables.secondaryDate.value = selectedDate ?: LocalDate.now()
                    showDatePicker1 = false
                },
                onDismiss = {
                    // close date picker when cancel is pressed
                    showDatePicker1 = false
                },
                date = dateB
            )
        }
    }
}

@Composable
fun Page(
    widgetClasses: List<Class<out Widget>>,
    locations: List<Place>,
    dates: List<LocalDate>,
    headerType: HeaderType = HeaderType.SINGLE,
    navController: NavHostController,
    onNavBarItemSelected: (Int) -> Unit,
    source: String,
    isMetric: Boolean
) {
    // get all requirements for the widgets
    val client: WeatherClient by remember {
        mutableStateOf(if (GlobalVariables.testing) MockClient() else OpenMeteoClient())
    }
    val widgets: List<Widget> = remember(client, widgetClasses) {
        widgetClasses.map {
            it.getConstructor(WeatherClient::class.java).newInstance(client)
        }
    }
    val status = remember { mutableStateOf(Status.LOADING) }
    var retry by remember { mutableStateOf(false) } // State for showing the retry button

    // get the data for the widgets
    val results = remember { mutableStateListOf<PlaceWeatherData>() }
    LaunchedEffect(locations, dates, retry, isMetric) {
        // check queried locations and dates for validity
        if (locations.isEmpty() || locations.find { it.id == -1 } != null) {
            status.value = Status.NO_LOCATION
        } else if (locations.find { it.id == -2 } != null) {
            status.value = Status.CURRENT_LOCATION_INVALID
        } else if (locations.size != dates.size) {
            status.value = Status.ERROR
        } else {
            // get the data
            status.value = Status.LOADING

            try {
                results.clear()
                if (isMetric) {
                    client.setMetric()
                } else {
                    client.setImperial()
                }
                locations.forEachIndexed { index, location ->
                    withTimeout(5000L) { // 5 seconds timeout
                        val result = client.getRange(
                            location.latitude,
                            location.longitude,
                            dates[index],
                        )
                        results.add(result)
                    }
                }

                status.value = if (results.size != locations.size
                    || results.find { !it.valid } != null
                ) {
                    Status.ERROR
                } else {
                    Status.SUCCESS
                }
            } catch (e: Exception) {
                results.clear()
                when (e) {
                    is TimeoutCancellationException -> {
                        status.value = Status.TIMEOUT
                    }

                    else -> {
                        status.value = Status.ERROR
                    }
                }
            }
        }

    }

    // draw the page

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.16f)
        ) {
            when (headerType) {
                HeaderType.SINGLE -> {
                    SingleHeader(
                        navController = navController,
                        onNavBarItemSelected = onNavBarItemSelected,
                        date = dates[0],
                        location = locations[0],
                        source = source
                    )
                }

                HeaderType.DOUBLE -> {
                    DoubleHeader(
                        navController = navController,
                        locationA = locations[0],
                        locationB = locations[1],
                        dateA = dates[0],
                        dateB = dates[1],
                        source = source,
                    )
                }
            }
        }

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        when (status.value) {
            Status.CURRENT_LOCATION_INVALID -> {
                // current Location is invalid
                //Text("Location has not been obtained yet")
                Box(
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(Modifier.align(Alignment.Center)) {
                        Icon(
                            Icons.Outlined.LocationOff,
                            contentDescription = "Place Icon",
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                        )
                        Text(
                            stringResource(R.string.err_current_location_invalid),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                        )
                        Button(
                            onClick = {
                                GlobalVariables.locationService.requestCurrentLocation()
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                stringResource(R.string.retry),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Status.NO_LOCATION -> {
                // no location selected / error on location select
                Box(
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(Modifier.align(Alignment.Center)) {
                        Icon(
                            Icons.Outlined.LocationOff,
                            contentDescription = "Place Icon",
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                        )
                        Text(
                            stringResource(R.string.err_no_location),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Status.LOADING -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            Status.SUCCESS -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    widgets.forEach { widget ->
                        widget.Draw(results, isMetric)
                    }
                }
            }

            Status.TIMEOUT -> {
// Show the retry button if the operation fails or times out
                Box(
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(Modifier.align(Alignment.Center)) {
                        Icon(
                            Icons.Outlined.WifiOff,
                            contentDescription = "Wifi Off Icon",
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                        )
                        Text(
                            stringResource(R.string.err_timeout),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                        )
                        Button(
                            onClick = {
                                retry = true
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(stringResource(R.string.retry), color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

            Status.ERROR -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(Modifier.align(Alignment.Center)) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error Icon",
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                        )
                        Text(
                            stringResource(R.string.err_unexpected),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                        )
                        Button(
                            onClick = {
                                retry = true
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }

}