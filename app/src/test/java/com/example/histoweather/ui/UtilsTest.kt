package com.example.histoweather.ui

import android.content.res.Configuration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.histoweather.api.geocoding.Place
import com.example.histoweather.api.weather.Day
import com.example.histoweather.api.weather.Hour
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDataModel
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.NoSuchElementException
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import java.time.temporal.ChronoUnit

class UtilsTest {
    @Test
    fun indexOfDateTimeInHours_listContainsElemOfSameHour() {
        val hours = listOf(
            Hour(LocalDateTime.of(2024, 12, 1, 19, 0), mapOf()),
            Hour(LocalDateTime.of(2024, 12, 1, 20, 0), mapOf()),
            Hour(LocalDateTime.of(2024, 12, 1, 21, 0), mapOf()), // <---
            Hour(LocalDateTime.of(2024, 12, 1, 22, 0), mapOf()),
        )
        val targetDateTime = LocalDateTime.of(2024, 12, 1, 21, 45)

        assertEquals(indexOfDateTimeInHours(hours, targetDateTime), 2)
    }
    @Test
    fun indexOfDateTimeInHours_listContainsExactElem() {
        var list = listOf(
            Hour(LocalDateTime.of(2024, 12, 1, 19, 0), mapOf()),
            Hour(LocalDateTime.of(2024, 12, 1, 20, 0), mapOf()),
            Hour(LocalDateTime.of(2024, 12, 1, 21, 0), mapOf()), // <---
            Hour(LocalDateTime.of(2024, 12, 1, 22, 0), mapOf()),
        )
        val targetDateTime = LocalDateTime.of(2024, 12, 1, 21, 0)

        assertEquals(indexOfDateTimeInHours(list, targetDateTime), 2)
    }

    @Test
    fun indexOfDateTimeInHours_listLacksElem() {
        var list = listOf(
            Hour(LocalDateTime.of(2024, 12, 1, 19, 0), mapOf()),
            Hour(LocalDateTime.of(2024, 12, 1, 20, 0), mapOf()),
            Hour(LocalDateTime.of(2024, 12, 1, 21, 0), mapOf()),
            Hour(LocalDateTime.of(2024, 12, 1, 22, 0), mapOf()),
        )
        val targetDateTime = LocalDateTime.of(2024, 12, 2, 0, 0)

        assertThrows(NoSuchElementException::class.java) {
            indexOfDateTimeInHours(list, targetDateTime)
        }
    }

    @Test
    fun indexOfDateInDays_listContainsElem() {
        var list = listOf(
            Day(LocalDate.of(2024, 12, 1), mapOf()), // <---
            Day(LocalDate.of(2024, 12, 2), mapOf()),
            Day(LocalDate.of(2024, 12, 3), mapOf()),
            Day(LocalDate.of(2024, 12, 4), mapOf()),
        )
        val targetDate = LocalDate.of(2024, 12, 1)

        assertEquals(indexOfDateInDays(list, targetDate), 0)
    }

    @Test
    fun indexOfDateInDays_listLacksElem() {
        var list = listOf(
            Day(LocalDate.of(2024, 12, 1), mapOf()),
            Day(LocalDate.of(2024, 12, 2), mapOf()),
            Day(LocalDate.of(2024, 12, 3), mapOf()),
            Day(LocalDate.of(2024, 12, 4), mapOf()),
        )
        val targetDate = LocalDate.of(2024, 12, 5)

        assertThrows(NoSuchElementException::class.java) {
            indexOfDateInDays(list, targetDate)
        }
    }

    @Test
    fun localTimeToString_now() {
        val localTime = LocalDateTime.now()
        val expected = "now"
        assertEquals(expected, localTimeToString(localTime))
    }

    @Test
    fun localTimeToString_past() {
        val pastDateTime = LocalDateTime.of(2023, 1, 1, 15, 0)
        val expected = "15:00"
        assertEquals(expected, localTimeToString(pastDateTime))
    }

    @Test
    fun localTimeToString_future() {
        val futureDateTime = LocalDateTime.of(2025, 1, 1, 23, 0)
        val expected = "23:00"
        assertEquals(expected, localTimeToString(futureDateTime))
    }

    @Test
    fun localDateToString_today() {
        val today = LocalDate.now()
        val expected = "Today"
        assertEquals(expected, localDateToString(today, includeYear = true))
    }

    @Test
    fun localDateToString_pastSameYear() {
        val pastDate = LocalDate.of(LocalDate.now().year, 1, 1)
        val expected = "01.01"
        assertEquals(expected, localDateToString(pastDate, includeYear = false))
    }

    @Test
    fun localDateToString_pastDifferentYear() {
        val pastDate = LocalDate.of(2023, 1, 1)
        val expected = "01.01.2023"
        assertEquals(expected, localDateToString(pastDate, includeYear = true, newLine = false))
    }

    @Test
    fun localDateToString_futureSameYear() {
        val futureDate = LocalDate.of(LocalDate.now().year, 12, 31)
        val expected = "31.12"
        assertEquals(expected, localDateToString(futureDate, includeYear = false))
    }

    @Test
    fun localDateToString_futureDifferentYear() {
        val futureDate = LocalDate.of(2027, 12, 31)
        val expected = "31.12.\n2027"
        assertEquals(expected, localDateToString(futureDate, includeYear = true, newLine = true))
    }

    @Test
    fun precipitationToString_metric(){
        val precipitation = 1.7f
        val metric = true
        val expected = "2 mm"
        assertEquals(expected, precipitationToString(precipitation, metric))
    }

    @Test
    fun precipitationToString_imperial(){
        val precipitation = 0.07f
        val metric = false
        val expected = "0.1 in"
        assertEquals(expected, precipitationToString(precipitation, metric))
    }

    @Test
    fun precipitationToString_imperialInteger(){
        val precipitation = 3.0f
        val metric = false
        val expected = "3 in"
        assertEquals(expected, precipitationToString(precipitation, metric))
    }

    @Test
    fun precipitationToString_null(){
        val precipitation = null
        val metric = true
        val expected = "?"
        assertEquals(expected, precipitationToString(precipitation, metric))
    }

    @Test
    fun rearrangeResults_allFavourites() {
        val favs = listOf(
            FavouritesDataModel(0, 1, "Place1", "US"),
            FavouritesDataModel(0, 2, "Place2", "US")
        )
        val srch = emptyList<SearchesDataModel>()
        val results = listOf(
            Place(3, "Place3", 0.0, 0.0),
            Place(4, "Place4", 0.0, 0.0)
        )

        val expected = listOf(
            Place(3, "Place3", 0.0, 0.0),
            Place(4, "Place4", 0.0, 0.0)
        )

        assertEquals(expected, rearrangeResults(favs, srch, results))
    }

    @Test
    fun rearrangeResults_allSearches() {
        val favs = emptyList<FavouritesDataModel>()
        val srch = listOf(
            SearchesDataModel(0, 1, "Place1", "US"),
            SearchesDataModel(0, 2, "Place2", "US")
        )
        val results = listOf(
            Place(1, "Place1", 0.0, 0.0),
            Place(2, "Place2", 0.0, 0.0)
        )

        val expected = listOf(
            Place(1, "Place1", 0.0, 0.0),
            Place(2, "Place2", 0.0, 0.0)
        )

        assertEquals(expected, rearrangeResults(favs, srch, results))
    }

    @Test
    fun rearrangeResults_emptyList() {
        val listFav = emptyList<FavouritesDataModel>()
        val listSrch = emptyList<SearchesDataModel>()
        val listRes = emptyList<Place>()

        val expected = emptyList<Place>()

        assertEquals(expected, rearrangeResults(listFav, listSrch, listRes))
    }

    @Test
    fun bringToFront_elemInList() {
        val list = mutableListOf(1, 2, 3)
        val elem = 2
        val expected = listOf(1, 3, 2)

        bringToFront(list, elem)
        assertEquals(expected, list)
    }

    @Test
    fun bringToFront_elemNotInList() {
        val list = mutableListOf(1, 2, 3)
        val elem = 4
        val expected = listOf(1, 2, 3, 4)

        bringToFront(list, elem)
        assertEquals(expected, list)
    }

    @Test
    fun isCurrentHour_sameHour() {
        val dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        assertTrue(isHour(dateTime, LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)))
    }

    @Test
    fun isCurrentHour_differentHour() {
        val dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        assertFalse(isHour(dateTime, LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1)))
    }

    @Test
    fun isCurrentHour_differentDay() {
        val dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        assertFalse(isHour(dateTime, LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusDays(4)))
    }

    @Test
    fun isCurrentHour_differentMonth() {
        val dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        assertFalse(isHour(dateTime, LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusMonths(3)))
    }

    @Test
    fun dpToPx_0dp() {
        val dp = 0.dp
        val density = Density(1.0f)
        assertEquals(0.0f, dpToPx(dp, density))
    }

    @Test
    fun dpToPx_1dp() {
        val dp = 1.dp
        val density = Density(1.0f)
        assertEquals(1.0f, dpToPx(dp, density))
    }

    @Test
    fun dpToPx_150dp() {
        val dp = 150.dp
        val density = Density(1.0f)
        assertEquals(150.0f, dpToPx(dp, density))
    }

    @Test
    fun pxToDp_0px() {
        val px = 0.0f
        val density = Density(1.0f)
        assertEquals(0.dp, pxToDp(px.toInt(), density))
    }

    @Test
    fun pxToDp_1px() {
        val px = 1.0f
        val density = Density(1.0f)
        assertEquals(1.dp, pxToDp(px.toInt(), density))
    }

    @Test
    fun pxToDp_150px() {
        val px = 150.0f
        val density = Density(1.0f)
        assertEquals(150.dp, pxToDp(px.toInt(), density))
    }

    @Test
    fun widthRatioToDp_0ratio() {
        val ratio = 0.0f
        val configuration = Configuration()
        configuration.screenWidthDp = 1080
        val density = Density(1.0f)
        assertEquals(0.dp, widthRatioToDp(ratio, configuration, density))
    }

    @Test
    fun widthRatioToDp_1ratio() {
        val ratio = 1.0f
        val configuration = Configuration()
        configuration.screenWidthDp = 1080
        val density = Density(1.0f)
        assertEquals(1080.dp, widthRatioToDp(ratio, configuration, density))
    }

    @Test
    fun widthRatioToDp_05ratio() {
        val ratio = 0.5f
        val configuration = Configuration()
        configuration.screenWidthDp = 1080
        val density = Density(1.0f)
        assertEquals(540.dp, widthRatioToDp(ratio, configuration, density))
    }

    @Test
    fun widthRatioToPx_0ratio() {
        val ratio = 0.0f
        val configuration = Configuration()
        configuration.screenWidthDp = 1080
        val density = Density(1.0f)
        assertEquals(0.0f, widthRatioToPx(ratio, configuration, density))
    }

    @Test
    fun widthRatioToPx_1ratio() {
        val ratio = 1.0f
        val configuration = Configuration()
        configuration.screenWidthDp = 1080
        val density = Density(1.0f)
        assertEquals(1080.0f, widthRatioToPx(ratio, configuration, density))
    }

    @Test
    fun widthRatioToPx_05ratio() {
        val ratio = 0.5f
        val configuration = Configuration()
        configuration.screenWidthDp = 1080
        val density = Density(1.0f)
        assertEquals(540.0f, widthRatioToPx(ratio, configuration, density))
    }

    @Test
    fun widthRatioToPx_05ratio_05density() {
        val ratio = 0.5f
        val configuration = Configuration()
        configuration.screenWidthDp = 1080
        val density = Density(0.5f)
        assertEquals(270.0f, widthRatioToPx(ratio, configuration, density))
    }

    @Test
    fun heightRatioToDp_0ratio() {
        val ratio = 0.0f
        val configuration = Configuration()
        configuration.screenHeightDp = 1920
        val density = Density(1.0f)
        assertEquals(0.dp, heightRatioToDp(ratio, configuration, density))
    }

    @Test
    fun heightRatioToDp_1ratio() {
        val ratio = 1.0f
        val configuration = Configuration()
        configuration.screenHeightDp = 1920
        val density = Density(1.0f)
        assertEquals(1920.dp, heightRatioToDp(ratio, configuration, density))
    }

    @Test
    fun heightRatioToDp_05ratio() {
        val ratio = 0.5f
        val configuration = Configuration()
        configuration.screenHeightDp = 1920
        val density = Density(1.0f)
        assertEquals(960.dp, heightRatioToDp(ratio, configuration, density))
    }

    @Test
    fun heightRatioToPx_0ratio() {
        val ratio = 0.0f
        val configuration = Configuration()
        configuration.screenHeightDp = 1920
        val density = Density(1.0f)
        assertEquals(0.0f, heightRatioToPx(ratio, configuration, density))
    }

    @Test
    fun heightRatioToPx_1ratio() {
        val ratio = 1.0f
        val configuration = Configuration()
        configuration.screenHeightDp = 1920
        val density = Density(1.0f)
        assertEquals(1920.0f, heightRatioToPx(ratio, configuration, density))
    }

    @Test
    fun heightRatioToPx_05ratio() {
        val ratio = 0.5f
        val configuration = Configuration()
        configuration.screenHeightDp = 1920
        val density = Density(1.0f)
        assertEquals(960.0f, heightRatioToPx(ratio, configuration, density))
    }

    @Test
    fun heightRatioToPx_05ratio_05density() {
        val ratio = 0.5f
        val configuration = Configuration()
        configuration.screenHeightDp = 1920
        val density = Density(0.5f)
        assertEquals(480.0f, heightRatioToPx(ratio, configuration, density))
    }

    @Test
    fun heightRatioToPx_05ratio_05density_1440height() {
        val ratio = 0.5f
        val configuration = Configuration()
        configuration.screenHeightDp = 1440
        val density = Density(0.5f)
        assertEquals(360.0f, heightRatioToPx(ratio, configuration, density))
    }
}