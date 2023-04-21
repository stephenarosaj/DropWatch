package edu.brown.cs.student.api.endpointHandlers;

import static org.testng.AssertJUnit.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.api.weatherHelpers.WeatherRequestProxy;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class WeatherHandlerTest {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  WeatherRequestProxy requestProxy = new WeatherRequestProxy(20, 20, 0.4);
  Type responseMapType = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> serializer =
      (new Moshi.Builder().build()).adapter(responseMapType);

  @BeforeEach
  public void setup() {
    // In fact, restart the entire Spark server for every test!
    requestProxy = new WeatherRequestProxy(20, 20, 0.4);
    Spark.get("/weather", new WeatherHandler(requestProxy));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/weather");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private Map<String, Object> tryRequest(String params) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/weather?" + params);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.connect();
    Map<String, Object> responseMap = null;
    if (clientConnection.getResponseCode() == 200) {
      responseMap = serializer.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    }
    return responseMap;
  }

  /**
   * Tests the response for valid set of coordinates.
   *
   * @throws IOException
   */
  @Test
  public void testValidRequest() throws IOException {
    // San Antonio, Texas
    Map<String, Object> response = tryRequest("lat=29.4252&lon=-98.4946");
    assertTrue(response.get("result").equals("success"));
    assertTrue(response.get("temperature_unit").equals("F"));
    assertTrue(response.get("latitude").equals("29.4252"));
    assertTrue(response.get("longitude").equals("-98.4946"));
  }

  /**
   * Tests the response for nonvalid numerical coordinates.
   *
   * @throws IOException
   */
  @Test
  public void testInvalidCoordinates() throws IOException {
    Map<String, Object> response = tryRequest("lat=1000&lon=1000");
    assertTrue(response.get("result").equals("error_data_retrieval"));
    assertTrue(response.get("latitude").equals("1000"));
    assertTrue(response.get("longitude").equals("1000"));
    assertTrue(response.get("details").equals("Error accessing location API: 400"));
  }

  /**
   * Tests the response for missing parameters.
   *
   * @throws IOException
   */
  @Test
  public void testMissingParams() throws IOException {
    Map<String, Object> response = tryRequest("lon=1000");
    assertTrue(response.get("result").equals("error_bad_request"));
    assertTrue(response.get("latitude").equals(""));
    assertTrue(response.get("details").equals("Must have latitude"));

    response = tryRequest("lat=1000");
    assertTrue(response.get("result").equals("error_bad_request"));
    assertTrue(response.get("longitude").equals(""));
    assertTrue(response.get("details").equals("Must have longitude"));
  }

  /**
   * Tests the response for non-numerical lat/long parameters.
   *
   * @throws IOException
   */
  @Test
  public void testNonnumericalParams() throws IOException {
    Map<String, Object> response = tryRequest("lat=beep&lon=boop");
    assertTrue(response.get("result").equals("error_bad_json"));
    assertTrue(response.get("latitude").equals("beep"));
    assertTrue(response.get("longitude").equals("boop"));
    assertTrue(response.get("details").equals("Latitude and longitude must be numbers"));
  }
}
