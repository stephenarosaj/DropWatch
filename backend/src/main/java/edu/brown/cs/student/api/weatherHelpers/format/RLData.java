package edu.brown.cs.student.api.weatherHelpers.format;

import com.squareup.moshi.Json;
import java.util.List;
import java.util.Map;

public record RLData(
    @Json(name = "type") String type, @Json(name = "features") List<Feature> features) {
  public record Feature(
      @Json(name = "type") String type,
      @Json(name = "geometry") Geometry geometry,
      @Json(name = "properties") Properties properties) {

    public record Geometry(
        @Json(name = "type") String type,
        @Json(name = "coordinates") List<List<List<List<Double>>>> coordinates) {}

    public record Properties(
        @Json(name = "state") String state,
        @Json(name = "city") String city,
        @Json(name = "name") String name,
        @Json(name = "holc_id") String holc_id,
        @Json(name = "holc_grade") String holc_grade,
        @Json(name = "neighborhood_id") Integer neighborhood_id,
        @Json(name = "isTarget") String isTarget,
        @Json(name = "area_description_data") Map<String, String> area_description_data) {}
  }
}
