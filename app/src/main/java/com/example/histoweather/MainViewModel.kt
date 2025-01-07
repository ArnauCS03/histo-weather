package com.example.histoweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.histoweather.data.favourites.FavouritesDao
import com.example.histoweather.data.favourites.FavouritesDataModel
import com.example.histoweather.data.searches.SearchesDao
import com.example.histoweather.data.searches.SearchesDataModel
import com.example.histoweather.data.settings.SettingsDao
import com.example.histoweather.data.settings.SettingsDataModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsDao: SettingsDao,
    private val favouritesDao: FavouritesDao,
    private val searchesDao: SearchesDao
) : ViewModel() {

    private val _isMetric = MutableStateFlow(true) // Default to Celsius
    val isMetric: StateFlow<Boolean?> get() = _isMetric

    private val _theme = MutableStateFlow<String?>(null) // Use null to indicate uninitialized state
    val theme: StateFlow<String?> get() = _theme

    private val _favouriteCities = MutableStateFlow<List<FavouritesDataModel>>(emptyList())
    val favouriteCities: StateFlow<List<FavouritesDataModel>> get() = _favouriteCities

    private val _searchedCities = MutableStateFlow<List<SearchesDataModel>>(emptyList())
    val searchedCities: StateFlow<List<SearchesDataModel>> get() = _searchedCities


    init {
        viewModelScope.launch {

            if (settingsDao.getSettingsCount() == 0) {
                settingsDao.insertSettings(SettingsDataModel())
            }

            // Load settings from the database
            val metric = settingsDao.isMetric() ?: true    // Default to Celsius if null
            val theme = settingsDao.getTheme() ?: "system" // Default to "system" if null

            _isMetric.value = metric
            _theme.value = theme

            // Load favourite cities
            _favouriteCities.value = favouritesDao.getAllFavourites()
            _searchedCities.value = searchesDao.getAllSearches()
        }
    }

    // Settings

    fun updateMetric(isMetric: Boolean) {
        viewModelScope.launch {
            val temperatureUnit = if (isMetric) 1 else 0
            settingsDao.updateTemperatureUnit(temperatureUnit.toString())
            _isMetric.value = isMetric
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            settingsDao.updateTheme(theme) // update only the theme
            _theme.value = theme
        }
    }

    // Favourites
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()
    fun updateFavourite(favourite: List<FavouritesDataModel>) {
        viewModelScope.launch {
            if (favouritesDao.countFavourites() >= 20) {
                _toastMessage.emit("Maximum number of favourites reached")
            } else {
                favouritesDao.upsertFavourite(favourite)
                _favouriteCities.value = favouritesDao.getAllFavourites()
            }
        }
    }


    fun removeFavourite(city: String, countryCode: String, idAPI: Int) {
        viewModelScope.launch {
            favouritesDao.deleteFavourite(city, countryCode, idAPI)
            _favouriteCities.value = favouritesDao.getAllFavourites()
        }
    }

    // Searches

    fun updateSearches(search: List<SearchesDataModel>) {
        viewModelScope.launch {
            if (searchesDao.countSearches() <= 20) {
                searchesDao.upsertSearches(search)
                _searchedCities.value = searchesDao.getAllSearches()
            }
        }
    }

    fun removeSearch(city: String, countryCode: String, idAPI: Int) {
        viewModelScope.launch {
            searchesDao.deleteSearch(city, countryCode, idAPI)
            _searchedCities.value = searchesDao.getAllSearches()
        }
    }
}