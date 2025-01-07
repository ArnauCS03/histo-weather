package com.example.histoweather.ui.pages


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.histoweather.GlobalVariables
import com.example.histoweather.ui.navbar.NavigationItem
import com.example.histoweather.ui.widgets.DailyComparisonWidget
import com.example.histoweather.ui.widgets.HourlyComparisonWidget

/**
 * Compare page
 */
@Composable
fun Compare(navController: NavHostController, onNavBarItemSelected: (Int) -> Unit, isMetric: Boolean) {
    Page(
        listOf(HourlyComparisonWidget::class.java, DailyComparisonWidget::class.java),
        listOf(GlobalVariables.primaryLocation.value, GlobalVariables.secondaryLocation.value),
        listOf(GlobalVariables.primaryDate.value, GlobalVariables.secondaryDate.value),
        headerType = HeaderType.DOUBLE,
        navController = navController,
        onNavBarItemSelected = onNavBarItemSelected,
        source = NavigationItem.Compare.route,
        isMetric = isMetric,
    )
}

