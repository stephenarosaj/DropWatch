/**
 * This class handles the /geo endpoint for the web API, which returns geoJSON data based on
 * user-specified latitude and longitude parameters. The returned data contains all geoJSON features
 * that fall within the specified latitude and longitude bounds. It also provides an optional
 * keyword parameter to filter features by a keyword contained in the feature's properties.
 */
package edu.brown.cs.student.api.endpointHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.api.JSONDeserializer;
import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.weatherHelpers.format.RLData;
import edu.brown.cs.student.api.weatherHelpers.format.RLData.Feature;
import edu.brown.cs.student.api.weatherHelpers.format.RLData.Feature.Geometry;
import edu.brown.cs.student.api.weatherHelpers.format.RLData.Feature.Properties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

public class GeoHandler implements Route {

  private RLData rawData;
  private List<String> history;

  /**
   * Initializes a GeoHandler object by reading in geoJSON data from a file and initializing an
   * empty search history list.
   *
   * @throws IOException if there is an issue reading in the geoJSON file
   */
  public GeoHandler(String filepath) throws IOException {
    JSONDeserializer<RLData> geoDeserializer =
        new JSONDeserializer<>(
            RLData.class, new Class[] {Feature.class, Geometry.class, Properties.class});
    String RLString = new String(Files.readAllBytes(Paths.get(filepath)));
    this.rawData =
        geoDeserializer.deserialize(
            new Buffer().readFrom(new ByteArrayInputStream(RLString.getBytes())));
    this.history = new ArrayList<>();
  }

  /**
   * Handles GET requests to the /geo endpoint by filtering the raw geoJSON data based on
   * user-specified latitude and longitude bounds, and returns the filtered data in JSON format.
   * Also provides an optional keyword parameter to filter features by a keyword contained in the
   * feature's properties.
   *
   * @param request the HTTP request sent to the server
   * @param response the HTTP response sent from the server
   * @return a JSON object containing filtered geoJSON data
   * @throws Exception if there is an issue handling the request
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String queryLatMin = request.queryParams("latMin");
    String queryLatMax = request.queryParams("latMax");
    String queryLonMin = request.queryParams("lonMin");
    String queryLonMax = request.queryParams("lonMax");

    // map representing the geoJSON object
    Map<String, Object> geoMap = new HashMap<>();

    // list of final features to be included
    List<Feature> filteredFeatures = new ArrayList<>();

    try {
      Double latMin = Double.parseDouble(queryLatMin);
      Double latMax = Double.parseDouble(queryLatMax);
      Double lonMin = Double.parseDouble(queryLonMin);
      Double lonMax = Double.parseDouble(queryLonMax);
      filteredFeatures = this.filter(latMin, latMax, lonMin, lonMax);
    } catch (NullPointerException e) {
      responseMap.put("details", "Must have a min/max latitude and min/max longitude");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    } catch (NumberFormatException e) {
      responseMap.put("details", "Can only have numbers in latitude and longitude");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    }

    String keyword = request.queryParams("keyword");
    if (keyword != null && !keyword.equals("")) {
      filteredFeatures = this.searchFilter(filteredFeatures, keyword);
      this.history.add(keyword);
      if (this.history.size() > 10) {
        this.history.remove(0);
      }
    }
    geoMap.put("features", filteredFeatures);
    geoMap.put("type", "FeatureCollection");
    responseMap.put("data", geoMap);
    responseMap.put("history", this.getHistory());
    Moshi moshi = new Builder().build();
    Type responseMapType = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(responseMapType);

    return MoshiUtil.serialize(responseMap, "success");
  }

  /**
   * Filters features based on latitude and longitude ranges.
   *
   * @param latMin the minimum latitude value of the range.
   * @param latMax the maximum latitude value of the range.
   * @param lonMin the minimum longitude value of the range.
   * @param lonMax the maximum longitude value of the range.
   * @return a list of filtered features.
   */
  public List<Feature> filter(double latMin, double latMax, double lonMin, double lonMax) {
    // instantiate holders for filtered data
    ArrayList<Feature> filteredFeatureList = new ArrayList<>();

    for (Feature feature : this.rawData.features()) {
      Geometry geometry = feature.geometry();
      if (geometry != null) {
        List<List<List<List<Double>>>> coordinates = geometry.coordinates();
        boolean inBounds = true;
        for (List<Double> coordinate : coordinates.get(0).get(0)) {
          // switched in the GeoJSON for some reason
          double lat = coordinate.get(1);
          double lon = coordinate.get(0);
          if (lat < latMin || lat > latMax || lon < lonMin || lon > lonMax) {
            inBounds = false;
            break;
          }
        }
        if (inBounds) {
          filteredFeatureList.add(feature);
        }
      }
    }

    return filteredFeatureList;
  }

  /**
   * Filters features based on a keyword search.
   *
   * @param locFilteredFeatures the features to be searched.
   * @param keyword the keyword to search for.
   * @return a list of filtered features.
   */
  public List<Feature> searchFilter(List<Feature> locFilteredFeatures, String keyword) {
    // example test query - first result should be true
    // http://localhost:3232/geo?latMin=33&latMax=34&lonMin=-87&lonMax=-86&keyword=Brook

    keyword = keyword.toLowerCase();

    List<Feature> searchFilteredFeatures = new ArrayList<>();
    for (Feature feature : locFilteredFeatures) {
      String isTarget = "false";
      for (String description : feature.properties().area_description_data().values()) {
        String[] words = description.replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");
        for (String word : words) {
          if (keyword.equals(word)) {
            isTarget = "true";
            break;
          }
        }
      }
      Properties newProps =
          new Properties(
              feature.properties().state(),
              feature.properties().city(),
              feature.properties().name(),
              feature.properties().holc_id(),
              feature.properties().holc_grade(),
              feature.properties().neighborhood_id(),
              isTarget,
              feature.properties().area_description_data());
      Feature toAdd = new Feature(feature.type(), feature.geometry(), newProps);
      searchFilteredFeatures.add(toAdd);
    }
    return searchFilteredFeatures;
  }

  /**
   * Returns a defensive copy of the history of filtered features.
   *
   * @return an ArrayList of strings representing the history of filtered features.
   */
  public ArrayList<String> getHistory() {
    // defensive copying
    return new ArrayList<>(this.history);
  }
}
