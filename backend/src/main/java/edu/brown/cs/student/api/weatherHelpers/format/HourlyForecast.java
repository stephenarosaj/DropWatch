package edu.brown.cs.student.api.weatherHelpers.format;

import com.squareup.moshi.Json;
import java.util.List;

/**
 * Record that stores the deserialized NWS Hourly Forecast JSON information.
 *
 * @param type String representing type of this JSON.
 * @param property The properties JSON of the forecast.
 */
public record HourlyForecast(
    @Json(name = "type") String type, @Json(name = "properties") HourlyProperty property) {

  /**
   * Record that stores the property JSON of the forecast.
   *
   * @param updated String representing the time the NWS forecast was updated.
   * @param periods List of Period records of the forecast for each hour in the day.
   */
  public record HourlyProperty(
      @Json(name = "updated") String updated, @Json(name = "periods") List<Period> periods) {

    /**
     * Record that stores the forecast JSON for one hour in a day.
     *
     * @param temperature String representing temperature for that hour.
     * @param tempUnit String representing unit of the temperature.
     */
    public record Period(
        @Json(name = "temperature") String temperature,
        @Json(name = "temperatureUnit") String tempUnit) {}
  }
}
