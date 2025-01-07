package com.example.histoweather.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.histoweather.api.geocoding.Place
import com.example.histoweather.api.weather.Day
import com.example.histoweather.api.weather.Hour
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDataModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

/**
 * function for date picker
 * @param onDateSelected function to be called when a date is selected
 * @param onDismiss function to be called when the date picker is dismissed (e.g. by pressing the cancel button)
 * @param date the initial date to be shown in the date picker
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    date: LocalDate = LocalDate.now()
) {
    val date_milis =
        date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    // yearRange is set to 1970 until the current year
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date_milis,
        yearRange = 1970..LocalDate.now().year
    )
    val context = LocalContext.current
    val toast = Toast.makeText(context, "Cannot select a date in the future", Toast.LENGTH_SHORT)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                val selectedDate = selectedDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                if (selectedDateMillis != null && selectedDateMillis < System.currentTimeMillis()) {
                    onDateSelected(selectedDate)
                    onDismiss()
                } else {
                    toast.show()
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("date_picker_dialog")
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * function to convert a LocalDateTime to a string
 * It returns either the time in HH:mm format or "now" if the time is the current hour
 */
fun localTimeToString(dateTime: LocalDateTime): String {
    val today = LocalDate.now()
    val now = LocalDateTime.now()
    if (dateTime.toLocalDate() == today && dateTime.hour == now.hour) {
        return "now"
    }
    val format = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(format)
}

/**
 * function to convert a LocalDate to a string
 * It returns either "today" if the date is the current day or the date in dd.MM format
 * or dd.MM.yyyy format if the year is not the current year
 * @param newLine if true, the date will be formatted with a line break between the date and the year
 */
fun localDateToString(date: LocalDate, includeYear: Boolean, newLine: Boolean = true): String {
    val today = LocalDate.now()
    if (date == today) {
        return "Today"
    }

    var format = ""
    if (!includeYear) {
        format = "dd.MM"
    } else {
        format = if (newLine) "dd.MM.\nyyyy" else "dd.MM.yyyy"

    }
    return date.format(DateTimeFormatter.ofPattern(format))
}

/**
 * function to convert a precipitation value to a string depending on the unit system
 */
fun precipitationToString(precipitation: Float?, metric: Boolean): String {
    if (precipitation == null) {
        return "?"
    }
    return if (metric) {
        "${precipitation.roundToInt()} mm"
    } else {
        // round to one decimal place
        val rounded = ((precipitation * 10).roundToInt().toFloat() / 10)
            .toString().trimEnd('0').trimEnd('.')

        "$rounded in"
    }
}

/**
 * function to bring an element to the front of a list or add it if it is not in the list
 */
fun <T> bringToFront(l: MutableList<T>, elem: T) {
    if (l.contains(elem)) l.remove(elem)
    l.add(elem)
}


/**
 * rearranges the search results of a geocoding query.
 * starts with favorites, then history, then the rest.
 */
fun rearrangeResults(
    favs: List<FavouritesDataModel>,
    srch: List<SearchesDataModel>,
    results: List<Place>
): List<Place> {
    var fav = mutableListOf<Place>()
    var hist = mutableListOf<Place>()
    var rest = mutableListOf<Place>()
    for (place in results) {
        if (favs.any { it.idAPI == place.id && it.city == place.name && it.countryCode == place.countryCode }) {
            fav.add(place)
        } else if (srch.any { it.idAPI == place.id && it.city == place.name && it.countryCode == place.countryCode }) {
            hist.add(place)
        } else {
            rest.add(place)
        }
    }
    return (fav + hist + rest).toList()
}

/**
 * function to check if a LocalDateTime is after a queried date
 */
fun isAfterSelectedDate(hour: LocalDateTime, queried: LocalDate): Boolean {
    return hour.toLocalDate() > queried
}

/**
 * function to check if an DateTime is a queried hour
 */
fun isHour(dateTime: LocalDateTime, queryHour: LocalDateTime): Boolean {
    return dateTime.truncatedTo(ChronoUnit.HOURS) == queryHour.truncatedTo(ChronoUnit.HOURS)
}

/**
 * function to check if a DateTime is in the current hour
 */
fun isCurrentHour(dateTime: LocalDateTime): Boolean {
    return isHour(dateTime, LocalDateTime.now())
}

/**
 * function to find the index of a LocalDateTime truncated to hours in a list of Hours
 * @throws NoSuchElementException if the list does not contain the searched element
 */
fun indexOfDateTimeInHours(l: List<Hour>, dateTime: LocalDateTime): Int {
    l.forEachIndexed { index, item ->
        if (isHour(item.time, dateTime)) {
            return index
        }
    }
    throw NoSuchElementException("The List does not contain the searched Element")
}

/**
 * function to find the index of a LocalDate in a list of Days
 * @throws NoSuchElementException if the list does not contain the searched element
 */
fun indexOfDateInDays(l: List<Day>, date: LocalDate): Int {
    l.forEachIndexed { index, item ->
        if (item.date == date) {
            return index
        }
    }
    throw NoSuchElementException("The List does not contain the searched Element")
}

/**
 * function to convert dp to px depending on the density
 * @param dp the value in dp
 * @param density the density of the device
 */
fun dpToPx(dp: Dp, density: Density): Float {
    return with(density) {
        dp.toPx()
    }
}

/**
 * function to convert px to dp depending on the density
 * @param px the value in px
 * @param density the density of the device
 */
fun pxToDp(px: Int, density: Density): Dp {
    return with(density) {
        px.toDp()
    }
}

/**
 * function to convert a ratio of the screen width to dp
 * @param ratio the ratio of the screen width
 * @param configuration the configuration of the device
 * @param density the density of the device
 */
fun widthRatioToDp(ratio: Float, configuration: Configuration, density: Density): Dp {
    if (ratio < 0.0 || ratio > 1.0) {
        throw IllegalArgumentException("Ratio has to be a value between 0 and 1")
    }

    val screenWidthDp = configuration.screenWidthDp

    return with(density) {
        (screenWidthDp * ratio).dp
    }
}

/**
 * function to convert a ratio of the screen width to px
 * @param ratio the ratio of the screen width
 * @param configuration the configuration of the device
 * @param density the density of the device
 */
fun widthRatioToPx(ratio: Float, configuration: Configuration, density: Density): Float {
    if (ratio < 0.0 || ratio > 1.0) {
        throw IllegalArgumentException("Ratio has to be a value between 0 and 1")
    }

    val widthDp = widthRatioToDp(ratio, configuration, density)

    // Convert to pixels
    return dpToPx(widthDp, density)
}

/**
 * function to convert a ratio of the screen height to dp
 * @param ratio the ratio of the screen height
 * @param configuration the configuration of the device
 * @param density the density of the device
 */
fun heightRatioToDp(ratio: Float, configuration: Configuration, density: Density): Dp {
    if (ratio < 0.0 || ratio > 1.0) {
        throw IllegalArgumentException("Ratio has to be a value between 0 and 1")
    }

    val screenHeightDp = configuration.screenHeightDp

    return with(density) {
        (screenHeightDp * ratio).dp
    }
}

/**
 * function to convert a ratio of the screen height to px
 * @param ratio the ratio of the screen height
 * @param configuration the configuration of the device
 * @param density the density of the device
 */
fun heightRatioToPx(ratio: Float, configuration: Configuration, density: Density): Float {
    if (ratio < 0.0 || ratio > 1.0) {
        throw IllegalArgumentException("Ratio has to be a value between 0 and 1")
    }

    val heightDp = heightRatioToDp(ratio, configuration, density)

    // Convert to pixels
    return dpToPx(heightDp, density)
}