package edu.brown.cs.student.api.weatherHelpers.format;

import com.squareup.moshi.Json;

/**
 * Record that stores the Location JSON of given coordinates.
 *
 * @param type String representing type of this JSON.
 * @param property The properties JSON of the location.
 */
public record Location(
    @Json(name = "type") String type, @Json(name = "properties") LocationProperty property) {

  /**
   * Record that stores the property JSON of the location.
   *
   * @param hourlyForecastURL String URL of the hourly forecastcast.
   */
  public record LocationProperty(@Json(name = "forecastHourly") String hourlyForecastURL) {}
}
