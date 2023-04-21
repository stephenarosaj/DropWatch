package edu.brown.cs.student.api.weatherHelpers.format;

import com.squareup.moshi.Json;
import java.util.List;

public record Pets(
    @Json(name = "CSV_Name") String CSVName,
    @Json(name = "henry") List<String> henryPets,
    @Json(name = "camila") List<String> camilaPets,
    @Json(name = "richard") List<String> richardPets,
    @Json(name = "pet_types") PetTypes petTypes) {
  public record PetTypes(
      @Json(name = "dog") List<String> dogBreeds, @Json(name = "cat") List<String> catBreeds) {}
}
