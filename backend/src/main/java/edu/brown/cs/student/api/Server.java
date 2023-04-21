package edu.brown.cs.student.api;

import static spark.Spark.after;

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



    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
