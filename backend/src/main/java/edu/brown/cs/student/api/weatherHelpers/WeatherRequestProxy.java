package edu.brown.cs.student.api.weatherHelpers;

import com.google.common.cache.*;
import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import edu.brown.cs.student.api.exceptions.DeserializeException;
import edu.brown.cs.student.api.weatherHelpers.format.HourlyForecast;
import edu.brown.cs.student.api.weatherHelpers.format.Location;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/** Class that acts as a cache layer between requests to our API and the NWS/external APIs. */
public class WeatherRequestProxy {
  private LoadingCache<Coordinates, HourlyForecast> cache;
  private double distance;

  /**
   * Creates a new cache layer.
   *
   * @param time Max amount of time a coordinate can live in the cache.
   * @param size Max number of coordinates cached.
   * @param distance Max distance between two coordinates for them to be considered close enough to
   *     use the same cached forecast info.
   */
  public WeatherRequestProxy(int time, int size, double distance) {
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(size)
            .recordStats()
            .expireAfterAccess(time, TimeUnit.MINUTES)
            .build(
                new CacheLoader<Coordinates, HourlyForecast>() {
                  @Override
                  public HourlyForecast load(Coordinates newLoc) {
                    return startRequest(newLoc);
                  }
                });
    this.distance = distance;
  }

  /**
   * Called when there has been a cache miss - handles loading new info into the cache. It searches
   * for close locations in the cache and then calls the NWS API if none have been found.
   *
   * @param newLoc Coordinates of new location to cache.
   * @return HourlyForecast of new coordinates.
   */
  private HourlyForecast startRequest(Coordinates newLoc) {
    ConcurrentMap<Coordinates, HourlyForecast> mapCache = cache.asMap();

    // search for close coordinates
    for (Coordinates cachedLoc : mapCache.keySet()) {
      if (newLoc.equals(cachedLoc)) {
        return mapCache.get(cachedLoc);
      }
    }
    // else make an API call
    return loadNewInfo(newLoc);
  }

  /**
   * Returns HourlyForecast from the NWS API for a location. If the location is invalid, the
   * HourlyForecast contains more info about the error.
   *
   * @param newLoc Coordinates of new location to request API for.
   * @return HourlyForecast from the API for this location.
   */
  private HourlyForecast loadNewInfo(Coordinates newLoc) {
    String locationURL =
        "https://api.weather.gov/points/" + newLoc.getLat() + "," + newLoc.getLon();

    RequestAPI locationRequest;
    Location location;

    try {
      locationRequest = new RequestAPI(locationURL);
    } catch (APIRequestException e) {
      return new HourlyForecast(
          "Error accessing location API: " + String.valueOf(e.getResponseCode()), null);
    }

    try {
      location = MoshiUtil.deserializeLocation(locationRequest.getJSON());
    } catch (DeserializeException e) {
      System.out.println(e.getMessage());
      return new HourlyForecast("Error deserializing location", null);
    }

    RequestAPI forecastRequest;
    try {
      forecastRequest = new RequestAPI(location.property().hourlyForecastURL());
    } catch (APIRequestException e) {
      return new HourlyForecast(
          "Error accessing forecast API: " + String.valueOf(e.getResponseCode()), null);
    }

    try {
      return MoshiUtil.deserializeForecast(forecastRequest.getJSON());
    } catch (DeserializeException e) {
      return new HourlyForecast("Error deserializing forecast", null);
    }
  }

  /**
   * Gets temperature and update time info for a latitude and longitude - handles cache checking and
   * loading.
   *
   * @param lat Latitude double.
   * @param lon Longitude double.
   * @return An Hourly Forecast record for the latitude and longitude.
   */
  public HourlyForecast getTempAndTime(double lat, double lon) {
    return cache.getUnchecked(new Coordinates(lat, lon, this.distance));
  }

  /**
   * Accessor for cache stats (used in testing).
   *
   * @return Stats for the weather cache.
   */
  public CacheStats cacheStats() {
    return cache.stats();
  }

  /**
   * Accessor for cache size (used in testing).
   *
   * @return Long representing cache size.
   */
  public long getCacheSize() {
    return cache.size();
  }
}
