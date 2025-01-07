package com.example.histoweather.ui.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.histoweather.GlobalVariables
import com.example.histoweather.ui.navbar.NavigationItem
import com.example.histoweather.ui.widgets.HourlyWidget
import com.example.histoweather.ui.widgets.TenDayWidget

/**
 * Historical Page
 */
@Composable
fun Historical(navController: NavHostController, onNavBarItemSelected: (Int) -> Unit, isMetric: Boolean) {
    Page(
        listOf(HourlyWidget::class.java, TenDayWidget::class.java),
        listOf(GlobalVariables.primaryLocation.value),
        listOf(GlobalVariables.primaryDate.value),
        headerType = HeaderType.SINGLE,
        navController = navController,
        onNavBarItemSelected = onNavBarItemSelected,
        source = NavigationItem.Historical.route,
        isMetric = isMetric,
    )

}
