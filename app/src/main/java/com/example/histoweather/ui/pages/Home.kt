package com.example.histoweather.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.histoweather.GlobalVariables
import com.example.histoweather.ui.navbar.NavigationItem
import com.example.histoweather.ui.widgets.CurrentWidget
import com.example.histoweather.ui.widgets.HourlyWidget
import com.example.histoweather.ui.widgets.TenDayWidget
import java.time.LocalDate


/**
 * Home page
 */
@Composable
fun Home(navController: NavHostController, onNavBarItemSelected: (Int) -> Unit, isMetric: Boolean) {
    // stores the selected date
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    Page(
        listOf(CurrentWidget::class.java, HourlyWidget::class.java, TenDayWidget::class.java),
        listOf(GlobalVariables.primaryLocation.value),
        listOf(currentDate),
        headerType = HeaderType.SINGLE,
        navController = navController,
        onNavBarItemSelected = onNavBarItemSelected,
        source = NavigationItem.Home.route,
        isMetric = isMetric,
    )
}