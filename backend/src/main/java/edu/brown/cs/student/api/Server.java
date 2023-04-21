package edu.brown.cs.student.api;

import static spark.Spark.after;

import edu.brown.cs.student.api.endpointHandlers.GeoHandler;
import edu.brown.cs.student.api.endpointHandlers.LoadHandler;
import edu.brown.cs.student.api.endpointHandlers.SearchHandler;
import edu.brown.cs.student.api.endpointHandlers.ViewHandler;
import edu.brown.cs.student.api.endpointHandlers.WeatherHandler;
import edu.brown.cs.student.api.weatherHelpers.WeatherRequestProxy;
import java.io.IOException;
import spark.Spark;

/** Main class for the Server. */
public class Server {

  /**
   * Main method that handles setting up the server and endpoints.
   *
   * @param args Array of args passed into main.
   */
  public static void main(String[] args) {
    Spark.port(3232);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    LoadHandler loadHandler = new LoadHandler();
    ViewHandler viewHandler = new ViewHandler(loadHandler);
    SearchHandler searchHandler = new SearchHandler(loadHandler);

    // cache with 20 spots, times out after 30 mins, c
    // oordinates are close togther if they're within 0.4 miles
    WeatherRequestProxy weatherRequestProxy = new WeatherRequestProxy(30, 20, 0.4);
    WeatherHandler weatherHandler = new WeatherHandler(weatherRequestProxy);

    Spark.get("loadcsv", loadHandler);
    Spark.get("viewcsv", viewHandler);
    Spark.get("searchcsv", searchHandler);
    Spark.get("weather", weatherHandler);

    try {
      GeoHandler geoHandler = new GeoHandler("data/redlining.json");
      Spark.get("geo", geoHandler);
    } catch (IOException e) {
      System.out.println("Unable to setup GeoHandler");
      System.out.println(e.getMessage());
    }

    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
