package edu.brown.cs.student.api.endpointHandlers;

import static org.testng.AssertJUnit.assertEquals;
import static spark.Spark.after;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class LoadHandlerTest {
  List<List<String>> loadContents = new ArrayList();
  static Moshi moshi;

  @BeforeAll
  public static void setup() {
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    Spark.get("loadcsv", new LoadHandler());
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
    Logger.getLogger("").setLevel(Level.WARNING);
    moshi = new Moshi.Builder().build();
  }

  public static Map<String, Object> getObject(String loadParam) throws IOException {
    String url = "http://localhost:" + Spark.port() + "/loadcsv?" + loadParam;
    URL requestConnection = new URL(url);
    HttpURLConnection clientConnection = (HttpURLConnection) requestConnection.openConnection();
    clientConnection.connect();
    Map<String, Object> responseMap = null;
    if (clientConnection.getResponseCode() == 200) {
      JsonAdapter serializer = moshi.adapter(Map.class);
      responseMap =
          (Map<String, Object>)
              serializer.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    }
    clientConnection.disconnect();
    return responseMap;
  }

  @Test
  public void loadNoPath() {
    try {
      Map<String, Object> resultMap = this.getObject("");
      assertEquals(resultMap.get("result"), "error_bad_request");
      assertEquals(resultMap.get("filepath"), "");
    } catch (IOException e) {
      assertEquals(false, true);
    }
  }

  @Test
  public void loadInvalidPath() {
    String filepath = "data/not-a.csv";
    try {
      Map<String, Object> resultMap = this.getObject("filepath=" + filepath);
      assertEquals(resultMap.get("result"), "error_datasource");
      assertEquals(resultMap.get("filepath"), filepath);
    } catch (IOException e) {
      assertEquals(false, true);
    }
  }

  @Test
  public void loadSuccessFile() {
    String filepath = "data/pets.csv";
    try {
      Map<String, Object> resultMap = this.getObject("filepath=" + filepath);
      assertEquals(resultMap.get("result"), "success");
      assertEquals(resultMap.get("filepath"), filepath);
    } catch (IOException e) {
      assertEquals(false, true);
    }
  }

  @Test
  public void loadMultipleCSV() {
    String filepathFirst = "data/cataloniacrash.csv";
    String filepathSecond = "data/pets.csv";
    try {
      Map<String, Object> resultMap = this.getObject("filepath=" + filepathFirst);
      assertEquals(resultMap.get("result"), "success");
      assertEquals(resultMap.get("filepath"), filepathFirst);

      Map<String, Object> resultMapNew = this.getObject("filepath=" + filepathSecond);
      assertEquals(resultMapNew.get("result"), "success");
      assertEquals(resultMapNew.get("filepath"), filepathSecond);
    } catch (IOException e) {
      assertEquals(false, true);
    }
  }
}
