/**
 * The GeoHandlerIntegrationTest class contains integration tests for the GeoHandler class, which
 * handles GET requests for geographical locations that are returned as GeoJSON objects. The tests
 * send HTTP requests to a Spark server running on localhost, with the handler mapped to the /geo
 * endpoint.
 */
package edu.brown.cs.student.api.endpointHandlers;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;
import spark.Spark;

public class GeoHandlerIntegrationTest {

  /** Sets up the Spark server to run on port 3232 before any tests are run. */
  @BeforeAll
  public static void setup_before_everything() {
    Service.ignite().port(3232);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Sets up the Spark server before each test, by adding the GeoHandler to the /geo endpoint. Also
   * waits for the server to start listening before continuing.
   *
   * @throws IOException if there is an issue connecting to the Spark server
   */
  @BeforeEach
  public void setup() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/geo", new GeoHandler("data/redlining.json"));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /**
   * Stops the Spark server and unmaps the GeoHandler from the /geo endpoint after each test. Also
   * waits for the server to stop listening before continuing.
   */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/weather");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  Type responseMapType = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> deserializer =
      (new Moshi.Builder().build()).adapter(responseMapType);

  /**
   * Attempts to make an HTTP request to the Spark server with the given parameters, and returns a
   * Map representing the response from the server.
   *
   * @param params the parameters for the HTTP GET request
   * @return a Map representing the JSON response from the server
   * @throws IOException if there is an issue connecting to the Spark server
   */
  private Map<String, Object> tryRequest(String params) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/geo?" + params);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.connect();
    Map<String, Object> responseMap = null;
    if (clientConnection.getResponseCode() == 200) {
      responseMap = deserializer.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    }
    return responseMap;
  }

  /**
   * Tests a basic valid request without searching.
   *
   * @throws IOException
   */
  @Test
  public void testValidLocFilterRequest() throws IOException {
    // San Antonio, Texas
    Map<String, Object> response =
        tryRequest("latMin=41.813227&latMax=41.830305&lonMin=-71.410832&lonMax=-71.376028");
    assertTrue(response.get("result").equals("success"));
    Map<String, Object> geoMap = (Map<String, Object>) response.get("data");
    List<Map<String, Object>> featureList = (List<Map<String, Object>>) geoMap.get("features");
    // should only return two areas
    assertTrue(featureList.size() == 2);
    // checks that the features are in the correct area
    Map<String, Object> geometry0 = (Map<String, Object>) featureList.get(0).get("geometry");
    List<List<List<List<Double>>>> coords0 =
        (List<List<List<List<Double>>>>) geometry0.get("coordinates");
    List<Double> expectedcoord0 = List.of(-71.383305, 41.826685);
    assertEquals(expectedcoord0, coords0.get(0).get(0).get(0));
  }

  /**
   * Integration testing for nonnumerical parameters and null parameters. Makes sure that an error
   * is returned when nonnumerical or null parameters are used, and that the correct number of
   * features are returned for a valid query with a keyword search. Also tests the history feature
   * to ensure that it persists across requests and that it is capped at a maximum size of 20.
   *
   * @throws IOException If there is an error in the input/output.
   * @throws InterruptedException If there is an error with the thread.
   */
  @Test
  public void testNonNumericalParams() throws IOException {
    Map<String, Object> response1 =
        tryRequest("latMin=hello&latMax=goodbye&lonMin=whacky&lonMax=fail");
    assertTrue(response1.get("result").equals("error_bad_request"));
    // checks that the features are in the correct area
    Map<String, Object> response2 = tryRequest("latMin=&latMax=&lonMin=&lonMax=");
    assertTrue(response2.get("result").equals("error_bad_request"));
    // try with a missing essential parameter
    Map<String, Object> response3 = tryRequest("latMin=&lonMin=&lonMax=");
    assertTrue(response3.get("result").equals("error_bad_request"));
  }

  /**
   * Tests persistence of search history on the backend!
   *
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testPersistence() throws InterruptedException, IOException {
    Map<String, Object> response =
        tryRequest(
            "latMin=41.813227&latMax=41.830305&lonMin=-71.410832&lonMax=-71.376028&keyword=hello");
    assertTrue(response.get("result").equals("success"));
    Map<String, Object> geoMap = (Map<String, Object>) response.get("data");
    List<Map<String, Object>> featureList = (List<Map<String, Object>>) geoMap.get("features");
    // should only return two areas
    assertTrue(featureList.size() == 2);
    // checks that the features are in the correct area
    Map<String, Object> geometry0 = (Map<String, Object>) featureList.get(0).get("geometry");
    List<List<List<List<Double>>>> coords0 =
        (List<List<List<List<Double>>>>) geometry0.get("coordinates");
    List<Double> expectedcoord0 = List.of(-71.383305, 41.826685);
    assertEquals(expectedcoord0, coords0.get(0).get(0).get(0));
    ArrayList l = new ArrayList();
    l.add("hello");
    assertTrue(response.get("history").equals(l));
    Thread.sleep(1000); // check persistence of history

    Map<String, Object> responseN =
        tryRequest(
            "latMin=41.813227&latMax=41.830305&lonMin=-71.410832&lonMax=-71.376028&keyword=whack");
    l.add("whack");
    assertTrue(responseN.get("history").equals(l));
  }
}
