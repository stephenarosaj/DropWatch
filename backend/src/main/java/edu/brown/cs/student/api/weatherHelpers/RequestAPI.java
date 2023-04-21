package edu.brown.cs.student.api.weatherHelpers;

import edu.brown.cs.student.api.exceptions.APIRequestException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import okio.Buffer;

/** Class that wraps a new request to an API. */
public class RequestAPI {
  Buffer JSON;
  int responseCode;

  /**
   * Requests the JSON at a certain API endpoint.
   *
   * @param requestURL URL to request.
   * @throws APIRequestException If there was a problen connecting to the endpoint.
   */
  public RequestAPI(String requestURL) throws APIRequestException {
    // API request call - should only be called if not found in cache
    HttpURLConnection clientConnection = null;
    responseCode = 200;
    try {
      URL requestConnection = new URL(requestURL);
      clientConnection = (HttpURLConnection) requestConnection.openConnection();
      clientConnection.connect();
      responseCode = clientConnection.getResponseCode();
      JSON = new Buffer().readFrom(clientConnection.getInputStream());
      clientConnection.disconnect();
    } catch (IOException e) {
      throw new APIRequestException("Something went wrong with the API.", responseCode);
    }
  }

  /**
   * Accessor for the JSON buffer at this API endpoint.
   *
   * @return Buffer JSON.
   */
  public Buffer getJSON() {
    return JSON;
  }

  /**
   * Accessor for the response code for connection attempt.
   *
   * @return Integer response code.
   */
  public int getResponseCode() {
    return responseCode;
  }
}
