package edu.brown.cs.student.api.endpointHandlers.ExternalAPI;

import edu.brown.cs.student.api.exceptions.APIRequestException;
import okio.Buffer;

/** TODO: comment */
public interface SpotifyDataSource {
  /** TODO: comment */
  public void setAccessToken(String token);

  public Buffer getData(String urlString) throws APIRequestException;
}
