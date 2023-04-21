package edu.brown.cs.student.api.endpointHandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

  public SearchHandler() {

  }


  @Override
  public Object handle(Request request, Response response) throws Exception {
    System.out.println("found endpoint");
    String client_id = "secret";
    String client_secret = "secret";
//    String command = "curl -X 'POST' 'https://accounts.spotify.com/api/token'"
//        + " -H 'Content-Type: application/x-www-form-urlencoded'"
//        + " -d 'grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret+"'";
//    try {
//      Process process = Runtime.getRuntime().exec(command);
//      process.getInputStream();
//
//      BufferedReader br = new BufferedReader(
//          new InputStreamReader(process.getInputStream()));
//
//      String output;
//      StringBuffer resp = new StringBuffer();
//
//      while ((output = br.readLine()) != null) {
//        resp.append(output);
//      }
//      br.close();
//
//      System.out.println(resp);
//
//    } catch (IOException ioException) {
////      ioException.printStackTrace();
//      System.out.println(ioException.getMessage());
//    }
    try {
      URL url = new URL("https://accounts.spotify.com/api/token");
      URLConnection urlc = url.openConnection();

      urlc.setDoOutput(true);
      urlc.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");

      OutputStreamWriter writer = new OutputStreamWriter(urlc.getOutputStream());

      writer.write("grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret);
      writer.flush();

      String line;
      BufferedReader reader = new BufferedReader(new
          InputStreamReader(urlc.getInputStream()));

      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      writer.close();
      reader.close();

    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
    return null;
  }
}
