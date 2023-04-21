package edu.brown.cs.student.api.endpointHandlers;

import static edu.brown.cs.student.api.endpointHandlers.GeoHandlerFuzzHelpers.FuzzHelpers.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertTrue;

import edu.brown.cs.student.api.JSONDeserializer;
import edu.brown.cs.student.api.weatherHelpers.format.RLData;
import edu.brown.cs.student.api.weatherHelpers.format.RLData.Feature;
import edu.brown.cs.student.api.weatherHelpers.format.RLData.Feature.Geometry;
import edu.brown.cs.student.api.weatherHelpers.format.RLData.Feature.Properties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import okio.Buffer;
import org.junit.jupiter.api.Test;

public class GeoHandlerUnitTest {

  private Buffer mockToBuffer(String mockFile) throws IOException {
    String mockedString = new String(Files.readAllBytes(Paths.get(mockFile)));
    return new Buffer().readFrom(new ByteArrayInputStream(mockedString.getBytes()));
  }

  private RLData mockToRLData(String mockFile) throws IOException {
    JSONDeserializer<RLData> geoDeserializer =
        new JSONDeserializer<>(
            RLData.class, new Class[] {Feature.class, Geometry.class, Properties.class});
    String RLString = new String(Files.readAllBytes(Paths.get(mockFile)));
    return geoDeserializer.deserialize(
        new Buffer().readFrom(new ByteArrayInputStream(RLString.getBytes())));
  }

  /** A basic test for filtering coordinates. */
  @Test
  public void ValidCoordinateTest() throws IOException {
    // test coordinates - should return two areas in Providence
    Double latMin = 41.813227;
    Double latMax = 41.830305;
    Double lonMin = -71.410832;
    Double lonMax = -71.376028;
    GeoHandler handler = new GeoHandler("data/redlining.json");
    List<Feature> actualResponse = handler.filter(latMin, latMax, lonMin, lonMax);
    List<Feature> expectedResponse =
        mockToRLData("data/mockedData/validCoordinateJSON.json").features();
    assertEquals(expectedResponse, actualResponse);
  }

  /** A basic test that invalid coordinates don't return anything. */
  @Test
  public void InvalidCoordinateTest() throws IOException {
    Double latMin = -200.00;
    Double latMax = -91.00;
    Double lonMin = -200.00;
    Double lonMax = -180.00;
    GeoHandler handler = new GeoHandler("data/redlining.json");
    List<Feature> actualResponse = handler.filter(latMin, latMax, lonMin, lonMax);
    List<Feature> expectedResponse = new ArrayList<>();
    assertEquals(expectedResponse, actualResponse);
  }

  /**
   * A basic searching test that checks that the search can find a target value and correctly note
   * that in the "isTarget" property of a JSON.
   *
   * @throws IOException
   */
  @Test
  public void SearchingTest() throws IOException {
    Double latMin = 33.1;
    Double latMax = 33.7;
    Double lonMin = -86.8;
    Double lonMax = -86.5;
    GeoHandler handler = new GeoHandler("data/redlining.json");
    List<Feature> locFilteredResponse = handler.filter(latMin, latMax, lonMin, lonMax);
    List<Feature> searchFilteredResponse = handler.searchFilter(locFilteredResponse, "brook");
    assertTrue(searchFilteredResponse.get(0).properties().isTarget().equals("true"));
    for (int i = 1; i < searchFilteredResponse.size(); i++) {
      assertTrue(searchFilteredResponse.get(i).properties().isTarget().equals("false"));
    }
  }

  /**
   * Test that should change number of iterations to fuzz test the entirety of the filter
   * functionality.
   */
  @Test
  public void randomTesting() {
    // only at 1 because it is highly time consuming. Should change to 1000 for proper
    for (int i = 0; i < 1; i++) {
      fuzzFilter();
    }
  }

  /**
   * Fuzz tester for the filter functionality from the GeoHandler class. Tests basically all
   * possible and reasonable lat, lon combos.
   */
  public void fuzzFilter() {
    try {
      Double latMin = randDouble(-90., 90.);
      Double latMax = randDouble(latMin, 90.);
      Double lonMax = randDouble(-180., 180.);
      Double lonMin = randDouble(-180., lonMax);
      GeoHandler handler = new GeoHandler("data/redlining.json");
      List<Feature> locFilteredResponse = handler.filter(latMin, latMax, lonMin, lonMax);
      List<Feature> searchFilteredResponse = handler.searchFilter(locFilteredResponse, "brook");
      //      assertTrue(searchFilteredResponse.get(0).properties().isTarget().equals("true"));
      assertTrue(featuresInBox(searchFilteredResponse, latMin, latMax, lonMin, lonMax));
    } catch (IOException e) {
      System.out.println("IO error in fuzz testing");
    }
  }

  @Test
  public void mockGeoTest() {
    try {
      Double latMin = 41.813227;
      Double latMax = 41.830305;
      Double lonMin = -71.410832;
      Double lonMax = -71.376028;
      GeoHandler handler = new GeoHandler("data/mockedData/mockRedlining.json");
      List<Feature> locFilteredResponse = handler.filter(latMin, latMax, lonMin, lonMax);
      assertEquals(locFilteredResponse, new ArrayList<>());
      List<Feature> searchFilteredResponse = handler.searchFilter(locFilteredResponse, "brook");
      assertEquals(searchFilteredResponse, new ArrayList<>());
      // demonstrate that filtering for a single point not in mocked json results in empty sets for
      // both

      latMin = -90.;
      latMax = 90.;
      lonMin = -180.;
      lonMax = 180.;
      // filtering accross full set results in a populated filter response
      locFilteredResponse = handler.filter(latMin, latMax, lonMin, lonMax);
      assertNotSame(locFilteredResponse, new ArrayList<>());
      searchFilteredResponse =
          handler.searchFilter(locFilteredResponse, "sdgdsgsdgdsgsdggsdg12f2e2efvv");
      // searching for something not in response results in empty
      assertTrue(searchFilteredResponse.get(0).properties().isTarget().equals("false"));
      // searching for something in the response indeed outputs a result with Brook in it
      searchFilteredResponse = handler.searchFilter(locFilteredResponse, "brook");
      assertTrue(searchFilteredResponse.get(0).properties().isTarget().equals("true"));

    } catch (IOException e) {
      System.out.println("IO error in fuzz testing");
    }
  }
}
