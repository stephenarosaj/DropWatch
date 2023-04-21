package edu.brown.cs.student.api;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.api.exceptions.DeserializeException;
import edu.brown.cs.student.api.weatherHelpers.format.HourlyForecast;
import edu.brown.cs.student.api.weatherHelpers.format.HourlyForecast.HourlyProperty;
import edu.brown.cs.student.api.weatherHelpers.format.HourlyForecast.HourlyProperty.Period;
import edu.brown.cs.student.api.weatherHelpers.format.Location;
import edu.brown.cs.student.api.weatherHelpers.format.Location.LocationProperty;
import java.lang.reflect.Type;
import java.util.Map;
import okio.Buffer;

/** Util class for moshi serializing and deserializing. */
public class MoshiUtil {

  /**
   * Serializes a given map with a response code.
   *
   * @param responseMap Map to populate.
   * @param responseCode Code to put in the "result" field.
   * @return String representation of the response map.
   */
  public static String serialize(Map<String, Object> responseMap, String responseCode) {
    responseMap.put("result", responseCode);
    Moshi moshi = new Builder().build();
    Type responseMapType = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(responseMapType);
    return adapter.toJson(responseMap);
  }

  /**
   * Deserializes a buffer JSON into a Location record.
   *
   * @param JSON A Buffer representing the JSON of the NWS location endpoint.
   * @return A Location record storing this JSON's attributes.
   * @throws DeserializeException If an exception has occurred in moshi's methods.
   */
  public static Location deserializeLocation(Buffer JSON) throws DeserializeException {
    try {
      Moshi moshi = new Builder().build();
      Type locationType = Types.newParameterizedType(Location.class, LocationProperty.class);
      JsonAdapter<Location> locationDeserializer = moshi.adapter(locationType);
      return locationDeserializer.fromJson(JSON);
    } catch (Exception e) {
      throw new DeserializeException("Location deserialization problem: " + e.getMessage());
    }
  }

  /**
   * Deserializes an hourly forecast JSON into an HourlyForecast record.
   *
   * @param JSON A Buffer representing the JSON of the NWS hourly forecast endpoint.
   * @return An HourlyForecast record storing this JSON's attributes.
   * @throws DeserializeException If an exception has occurred in moshi's methods.
   */
  public static HourlyForecast deserializeForecast(Buffer JSON) throws DeserializeException {
    try {
      Moshi moshi = new Builder().build();
      Type forecastType =
          Types.newParameterizedType(HourlyForecast.class, HourlyProperty.class, Period.class);
      JsonAdapter<HourlyForecast> forecastDeserializer = moshi.adapter(forecastType);
      return forecastDeserializer.fromJson(JSON);
    } catch (Exception e) {
      throw new DeserializeException("Forecast deserialization problem: " + e.getMessage());
    }
  }
}
