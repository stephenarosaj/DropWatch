package edu.brown.cs.student.api.weatherHelpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.assertTrue;

import edu.brown.cs.student.api.exceptions.APIRequestException;
import org.junit.jupiter.api.Test;

public class RequestAPITest {

  /**
   * Tests connecting to a valid API endpoint.
   *
   * @throws APIRequestException
   */
  @Test
  public void testValidConnection() throws APIRequestException {
    RequestAPI request = new RequestAPI("https://api.weather.gov/points/41.8268,-71.4029");
    assertEquals(request.getResponseCode(), 200);
  }

  /** Tests connecting to an endpoint with nonvalid params. */
  @Test
  public void testInvalidCoordinatesThrowsException() {
    APIRequestException exception =
        assertThrows(
            APIRequestException.class,
            () -> {
              RequestAPI request = new RequestAPI("https://api.weather.gov/points/1000,1000");
            });
    assertEquals(exception.getResponseCode(), 400);
    assertTrue(exception.getMessage().equals("Something went wrong with the API."));
  }

  /** Tests connecting to an endpoint with valid params, but NWS has no info. */
  @Test
  public void testValidOutsideUSThrowsException() {
    APIRequestException exception =
        assertThrows(
            APIRequestException.class,
            () -> {
              RequestAPI request = new RequestAPI("https://api.weather.gov/points/34.0522,118.24");
            });
    assertEquals(exception.getResponseCode(), 404);
    assertTrue(exception.getMessage().equals("Something went wrong with the API."));
  }
}
