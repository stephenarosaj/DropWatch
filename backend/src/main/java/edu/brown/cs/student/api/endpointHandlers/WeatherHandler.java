package edu.brown.cs.student.api.endpointHandlers;

// using Location class instead of WeatherFormat

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.weatherHelpers.WeatherRequestProxy;
import edu.brown.cs.student.api.weatherHelpers.format.HourlyForecast;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class that handles weather requests. */
public class WeatherHandler implements Route {
  private WeatherRequestProxy requestProxy;

  /**
   * Creates a WeatherHandler.
   *
   * @param requestProxy Cache proxy layer to request info from.
   */
  public WeatherHandler(WeatherRequestProxy requestProxy) {
    this.requestProxy = requestProxy;
  }

  /**
   * Handles requests to get forecast for latitude and longitude.
   *
   * @param request Browser HTTP request; should contain latitude and longitude parameters.
   * @param response The response object providing functionality for modifying the response.
   * @return String JSON returning result code, the temperature, unit, parameters, and date/time of
   *     NWS forecast update.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<String, Object>();

    String latitude = request.queryParams("lat");
    String longitude = request.queryParams("lon");

    // error checking
    if (latitude == null || latitude.equals("")) {
      responseMap.put("latitude", "");
      responseMap.put("details", "Must have latitude");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    }
    responseMap.put("latitude", latitude);

    if (longitude == null || longitude.equals("")) {
      responseMap.put("longitude", "");
      responseMap.put("details", "Must have longitude");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    }
    responseMap.put("longitude", longitude);

    // start getting info for latitude and longitude
    double lat, lon;
    try {
      lat = Double.parseDouble(latitude);
      lon = Double.parseDouble(longitude);
    } catch (NumberFormatException e) {
      responseMap.put("details", "Latitude and longitude must be numbers");
      return MoshiUtil.serialize(responseMap, "error_bad_json");
    }

    return getForecastResponse(responseMap, lat, lon);
  }

  /**
   * Requests the forecast info from the proxy layer.
   *
   * @param responseMap Map to populate.
   * @param lat Latitude of request.
   * @param lon Longitude of request.
   * @return String JSON with forecast info, or error info otherwise.
   */
  private String getForecastResponse(Map<String, Object> responseMap, double lat, double lon) {
    HourlyForecast forecast = requestProxy.getTempAndTime(lat, lon);

    if (forecast.type().equals("Feature")) {
      // the lat/lon pair are associated with a valid temperature
      responseMap.put("temperature", forecast.property().periods().get(0).temperature());
      responseMap.put("temperature_unit", forecast.property().periods().get(0).tempUnit());
      String[] dateTime = forecast.property().updated().split("T");
      responseMap.put("date", dateTime[0]);
      responseMap.put("time", dateTime[1]);
      return MoshiUtil.serialize(responseMap, "success");
    } else {
      // the API call failed or deserialization failed
      responseMap.put("details", forecast.type());
      return MoshiUtil.serialize(responseMap, "error_data_retrieval");
    }
  }
}
