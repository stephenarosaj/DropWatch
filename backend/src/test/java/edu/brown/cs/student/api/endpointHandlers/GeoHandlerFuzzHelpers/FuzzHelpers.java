package edu.brown.cs.student.api.endpointHandlers.GeoHandlerFuzzHelpers;

import edu.brown.cs.student.api.weatherHelpers.format.RLData.Feature;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** Class used to generate random URL for fuzz testing */
public class FuzzHelpers {
  private static char[] nums; // 0-9, ., -
  private static char[] URLchars; // 0-9, letters, special chars

  public FuzzHelpers() {

    String lowercase = "abcdefghijklmnopqrstuvwxyz";
    String uppercase = lowercase.toUpperCase(Locale.ROOT);
    String nums = "0123456789";
    String letterNum = lowercase + uppercase + nums;
    String URLchars = nums + "-._~";

    this.nums = (nums + ".-").toCharArray();
    this.URLchars = URLchars.toCharArray();
  }

  /**
   * @param charSet - set of chars output will be chosen from
   * @param length - bounds length of output string
   * @return string with a random set of chars from charSet of length less than seconde param length
   */
  private static String getRandomSet(char[] charSet, int length) {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    int string_len = r.nextInt(length + 1);
    char[] out = new char[string_len];
    for (int i = 0; i < out.length; i++) {
      out[i] = charSet[r.nextInt(charSet.length)];
    }
    return String.copyValueOf(out);
  }

  /**
   * @param length - bound length of string
   * @return a random string using valid URL characters
   */
  public static String getRandomURL(int length) {
    return getRandomSet(URLchars, length);
  }

  /**
   * @param length - bound length of string
   * @return a string made up of random digits and special characters "." and "-"
   */
  public static String getRandomNum(int length) {
    return getRandomSet(nums, length);
  }

  /**
   * @param length - bound length of string
   * @returna a string made up of random chars from UTF-8
   */
  public static String getRandomString(int length) {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    byte[] bytes = new byte[length];
    r.nextBytes(bytes);
    return new String(bytes, Charset.forName("UTF-8"));
  }

  /**
   * @param length - bound length of string
   * @return a string made up of random chars that are url encoded
   */
  public static String getRandomStringEncode(int length) {
    String norm = getRandomString(length);
    return URLEncoder.encode(norm, StandardCharsets.UTF_8);
  }

  // https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
  public static Double randDouble(Double lower, Double upper) {
    Random r = new Random();
    Double randomValue = lower + (upper - lower) * r.nextDouble();
    return randomValue;
  }

  /**
   * Checks if a list of features falls within a given latitude and longitude box.
   *
   * @param features A list of Feature objects to check.
   * @param latMin The minimum latitude of the bounding box.
   * @param latMax The maximum latitude of the bounding box.
   * @param lonMin The minimum longitude of the bounding box.
   * @param lonMax The maximum longitude of the bounding box.
   * @return True if all features fall within the given box, false otherwise.
   */

  // helps with fuzz testing at unit level
  public static Boolean featuresInBox(
      List<Feature> features, Double latMin, Double latMax, Double lonMin, Double lonMax) {
    for (Feature feature : features) {
      List<List<List<List<Double>>>> coords = feature.geometry().coordinates();
      for (List<Double> coordinate : coords.get(0).get(0)) {
        double lat = coordinate.get(1);
        double lon = coordinate.get(0);
        if (lat < latMin || lat > latMax || lon < lonMin || lon > lonMax) {
          return false;
        }
      }
    }
    return true;
  }
}
