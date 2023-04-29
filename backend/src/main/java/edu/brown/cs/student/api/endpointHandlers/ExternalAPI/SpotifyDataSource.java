package edu.brown.cs.student.api.endpointHandlers.ExternalAPI;


import edu.brown.cs.student.api.exceptions.APIRequestException;
import okio.Buffer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public interface SpotifyDataSource {

    public Buffer getData(String urlString) throws APIRequestException;
}
