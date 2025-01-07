package com.example.histoweather.api

class OpenMeteoParameters private constructor() { // Prevent instantiation
    /***
     * Contains most parameters of OpenMeteoAPI.
     * Can be used as a reference or to limit parameters only to valid ones
     */
    companion object {

        val baseURLs = mapOf(
            "historical" to "https://archive-api.open-meteo.com/v1/archive?",
            "forecast" to "https://api.open-meteo.com/v1/forecast?",
            "current" to "https://api.open-meteo.com/v1/forecast?",
            "geocoding" to "https://geocoding-api.open-meteo.com/v1/search?",
            "geoid" to "https://geocoding-api.open-meteo.com/v1/get?",
        )

        val weatherCodesMap = mapOf<String, String>(
            "0" to "\u2600", // Clear sky ☀
            "1" to "\uD83C\uDF24", // Mainly clear 🌤
            "2" to "\u26C5", // Partly cloudy ⛅
            "3" to "\u2601", // Overcast ☁
            "45" to "\uD83C\uDF2B", // Fog 🌫
            "48" to "\uD83C\uDF2B", // Depositing rime fog 🌫
            "51" to "\uD83C\uDF27", // Drizzle: Light 🌧
            "53" to "\uD83C\uDF27", // Drizzle: Moderate 🌧
            "55" to "\uD83C\uDF27", // Drizzle: Dense 🌧
            "56" to "\u2744", // Freezing Drizzle: Light ❄
            "57" to "\u2744", // Freezing Drizzle: Dense ❄
            "61" to "\uD83C\uDF26", // Rain: Slight 🌦
            "63" to "\uD83C\uDF26", // Rain: Moderate 🌦
            "65" to "\uD83C\uDF26", // Rain: Heavy 🌦
            "66" to "\u2744", // Freezing Rain: Light ❄
            "67" to "\u2744", // Freezing Rain: Heavy ❄
            "71" to "\uD83C\uDF28", // Snow fall: Slight 🌨
            "73" to "\uD83C\uDF28", // Snow fall: Moderate 🌨
            "75" to "\uD83C\uDF28", // Snow fall: Heavy 🌨
            "77" to "\u2744", // Snow grains ❄
            "80" to "\uD83C\uDF26", // Rain showers: Slight 🌦
            "81" to "\uD83C\uDF26", // Rain showers: Moderate 🌦
            "82" to "\uD83C\uDF26", // Rain showers: Violent 🌦
            "85" to "\uD83C\uDF28", // Snow showers: Slight 🌨
            "86" to "\uD83C\uDF28", // Snow showers: Heavy 🌨
            "95" to "\u26A1", // Thunderstorm: Slight or moderate ⚡
            "96" to "\u26A1", // Thunderstorm with slight hail ⚡
            "99" to "\u26A1" // Thunderstorm with heavy hail ⚡
        )


        /*
        Settings
         */

        // timezone
        val timezones = arrayOf(
            "America%2FAnchorage",
            "America%2FLos_Angeles",
            "America%2FDenver",
            "America%2FChicago",
            "America%2FNew_York",
            "America%2FSao_Paulo",
            "GMT",
            "auto",
            "Europe%2FLondon",
            "Europe%2FBerlin",
            "Europe%2FMoscow",
            "Africa%2FCairo",
            "Asia%2FBangkok",
            "Asia%2FSingapore",
            "Asia%2FTokyo",
            "Australia%2FSydney",
            "Pacific%2FAuckland",
        )

        // temperature_unit
        val temperatureUnits = arrayOf(
            "celsius",
            "fahrenheit",
        )

        // wind_speed_unit
        val windSpeedUnits = arrayOf(
            "kmh",
            "ms",
            "mph",
            "kn",
        )

        // precipitation_unit
        val precipitationUnits = arrayOf(
            "mm",
            "inch",
        )

        /*
        Historical parameters
         */

        // hourly
        val historicalHourlyParameters = arrayOf(
            "temperature_2m",
            "relative_humidity_2m",
            "dew_point_2m",
            "apparent_temperature",
            "precipitation",
            "rain",
            "snowfall",
            "snow_depth",
            "weather_code",
            "pressure_msl",
            "surface_pressure",
            "cloud_cover",
            "cloud_cover_low",
            "cloud_cover_mid",
            "cloud_cover_high",
            "et0_fao_evapotranspiration",
            "vapour_pressure_deficit",
            "wind_speed_10m",
            "wind_speed_100m",
            "wind_direction_10m",
            "wind_direction_100m",
            "wind_gusts_10m",
            "soil_temperature_0_to_7cm",
            "soil_temperature_7_to_28cm",
            "soil_temperature_28_to_100cm",
            "soil_temperature_100_to_255cm",
            "soil_moisture_0_to_7cm",
            "soil_moisture_7_to_28cm",
            "soil_moisture_28_to_100cm",
            "soil_moisture_100_to_255cm",

            // Additional Variables and Options
            "boundary_layer_height",
            "wet_bulb_temperature_2m",
            "total_column_integrated_water_vapour",
            "is_day",
            "sunshine_duration",

            // Solar Radiation Variables
            "shortwave_radiation",
            "direct_radiation",
            "diffuse_radiation",
            "direct_normal_irradiance",
            "global_tilted_irradiance",
            "terrestrial_radiation",
            "shortwave_radiation_instant",
            "direct_radiation_instant",
            "diffuse_radiation_instant",
            "direct_normal_irradiance_instant",
            "global_tilted_irradiance_instant",
            "terrestrial_radiation_instant",

            // ERA5-Ensemble Spread Variables
            "temperature_2m_spread",
            "dew_point_2m_spread",
            "precipitation_spread",
            "snowfall_spread",
            "shortwave_radiation_spread",
            "direct_radiation_spread",
            "pressure_msl_spread",
            "cloud_cover_low_spread",
            "cloud_cover_mid_spread",
            "cloud_cover_high_spread",
            "wind_speed_10m_spread",
            "wind_speed_100m_spread",
            "wind_direction_10m_spread",
            "wind_direction_100m_spread",
            "wind_gusts_10m_spread",
            "soil_temperature_0_to_7cm_spread",
            "soil_temperature_7_to_28cm_spread",
            "soil_temperature_28_to_100cm_spread",
            "soil_temperature_100_to_255cm_spread",
            "soil_moisture_0_to_7cm_spread",
            "soil_moisture_7_to_28cm_spread",
            "soil_moisture_28_to_100cm_spread",
            "soil_moisture_100_to_255cm_spread",
        )

        // daily
        val historicalDailyParameters = arrayOf(
            "weather_code",
            "temperature_2m_max",
            "temperature_2m_min",
            "temperature_2m_mean",
            "apparent_temperature_max",
            "apparent_temperature_min",
            "apparent_temperature_mean",
            "sunrise",
            "sunset",
            "daylight_duration",
            "sunshine_duration",
            "precipitation_sum",
            "rain_sum",
            "snowfall_sum",
            "precipitation_hours",
            "wind_speed_10m_max",
            "wind_gusts_10m_max",
            "wind_direction_10m_dominant",
            "shortwave_radiation_sum",
            "et0_fao_evapotranspiration",
        )

        /*
        Forecast parameters
         */

        // hourly
        val forecastHourlyParameters = arrayOf(
            "temperature_2m",
            "relative_humidity_2m",
            "dew_point_2m",
            "apparent_temperature",
            "precipitation_probability",
            "precipitation",
            "rain",
            "showers",
            "snowfall",
            "snow_depth",
            "weather_code",
            "pressure_msl",
            "surface_pressure",
            "cloud_cover",
            "cloud_cover_low",
            "cloud_cover_mid",
            "cloud_cover_high",
            "visibility",
            "evapotranspiration",
            "et0_fao_evapotranspiration",
            "vapour_pressure_deficit",
            "wind_speed_10m",
            "wind_speed_80m",
            "wind_speed_120m",
            "wind_speed_180m",
            "wind_direction_10m",
            "wind_direction_80m",
            "wind_direction_120m",
            "wind_direction_180m",
            "wind_gusts_10m",
            "temperature_80m",
            "temperature_120m",
            "temperature_180m",
            "soil_temperature_0cm",
            "soil_temperature_6cm",
            "soil_temperature_18cm",
            "soil_temperature_54cm",
            "soil_moisture_0_to_1cm",
            "soil_moisture_1_to_3cm",
            "soil_moisture_3_to_9cm",
            "soil_moisture_9_to_27cm",
            "soil_moisture_27_to_81cm",

            // Additional Variables and Options

            "uv_index",
            "uv_index_clear_sky",
            "is_day",
            "sunshine_duration",
            "wet_bulb_temperature_2m",
            "total_column_integrated_water_vapour",
            "cape",
            "lifted_index",
            "convective_inhibition",
            "freezing_level_height",
            "boundary_layer_height",

            // Solar Radiation Variables
            "shortwave_radiation",
            "direct_radiation",
            "diffuse_radiation",
            "direct_normal_irradiance",
            "global_tilted_irradiance",
            "terrestrial_radiation",
            "shortwave_radiation_instant",
            "direct_radiation_instant",
            "diffuse_radiation_instant",
            "direct_normal_irradiance_instant",
            "global_tilted_irradiance_instant",
            "terrestrial_radiation_instant",
        )

        // daily
        val forecastDailyParameters = arrayOf(
            "weather_code",
            "temperature_2m_max",
            "temperature_2m_min",
            "temperature_2m_mean",
            "apparent_temperature_max",
            "apparent_temperature_min",
            "apparent_temperature_mean",
            "sunrise",
            "sunset",
            "daylight_duration",
            "sunshine_duration",
            "precipitation_sum",
            "rain_sum",
            "snowfall_sum",
            "precipitation_hours",
            "wind_speed_10m_max",
            "wind_gusts_10m_max",
            "wind_direction_10m_dominant",
            "shortwave_radiation_sum",
            "et0_fao_evapotranspiration",
        )

        /*
        Current parameters
         */

        // current
        val currentParameters = arrayOf(
            "temperature_2m",
            "relative_humidity_2m",
            "apparent_temperature",
            "is_day",
            "precipitation",
            "rain",
            "showers",
            "snowfall",
            "weather_code",
            "cloud_cover",
            "pressure_msl",
            "surface_pressure",
            "wind_speed_10m",
            "wind_direction_10m",
            "wind_gusts_10m",
        )

    }
}