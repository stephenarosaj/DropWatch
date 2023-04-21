package edu.brown.cs.student.api.weatherHelpers;

import static org.testng.AssertJUnit.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CoordinatesTest {

  /** Tests that two coordinates that are close are registered as "equal". */
  @Test
  public void testCloseEquals() {
    Coordinates coor1 = new Coordinates(41.8240, -71.4128, 0.5);
    Coordinates coor2 = new Coordinates(41.9, -71.0, 0.5);
    assertEquals(coor1, coor2);
  }

  /**
   * Tests that two different coordinates representing the same lat/lon get mapped to the same
   * hashcode.
   */
  @Test
  public void testHashCode() {
    Coordinates coor1 = new Coordinates(41.8240, -71.4128, 0.5);
    Coordinates coor2 = new Coordinates(41.8240, -71.4128, 0.5);
    Map<Coordinates, String> testMap = new HashMap<>();
    testMap.put(coor1, "providence_lat_lon");

    assertEquals(testMap.get(coor2), "providence_lat_lon");
  }
}
