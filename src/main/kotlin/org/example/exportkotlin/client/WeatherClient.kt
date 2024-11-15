package org.example.exportkotlin.client

import org.example.exportkotlin.client.dto.WeatherDto
import org.example.exportkotlin.exception.CustomApiException
import org.example.exportkotlin.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class WeatherClient {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getTodoWeather(): String {
        val weatherList = RestClient.create()
            .get()
            .uri(buildWeatherApiUri())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(object : ParameterizedTypeReference<List<WeatherDto>>() {})!!
        log.info(weatherList.toString())

        weatherList.forEach { weatherDto ->
            if (getCurrentDate() == weatherDto.date) {
                return weatherDto.weather
            }
        }

        throw CustomApiException(ErrorCode.WEATHER_NOT_FOUND)
    }

    fun buildWeatherApiUri(): URI {
        return UriComponentsBuilder
            .fromUriString("https://f-api.github.io")
            .path("/f-api/weather.json")
            .encode()
            .build()
            .toUri();
    }

    fun getCurrentDate(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"))
    }
}