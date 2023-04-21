package edu.brown.cs.student.api.weatherHelpers;

import static org.testng.AssertJUnit.assertEquals;

import org.junit.jupiter.api.Test;

public class WeatherRequestProxyTest {

  /**
   * Tests that the cache is properly finding keys when they exist and registering misses when they
   * don't.
   */
  @Test
  public void testCacheHitAndMiss() {
    WeatherRequestProxy proxy = new WeatherRequestProxy(30, 5, 0.5);
    proxy.getTempAndTime(40, -95);
    assertEquals(1, proxy.cacheStats().missCount());
    proxy.getTempAndTime(40, -95);
    assertEquals(1, proxy.cacheStats().hitCount());
    proxy.getTempAndTime(40, -95);
    assertEquals(2, proxy.cacheStats().hitCount());
    proxy.getTempAndTime(71, -49);
    assertEquals(2, proxy.cacheStats().missCount());
    assertEquals(proxy.getCacheSize(), 2);
  }

  /** Tests that the cache is properly evicting entries when the size is exceeded. */
  @Test
  public void testCacheEviction() {
    WeatherRequestProxy proxy = new WeatherRequestProxy(5, 2, 0.5);
    proxy.getTempAndTime(40, -95);
    proxy.getTempAndTime(50, -95);
    assertEquals(2, proxy.getCacheSize());
    proxy.getTempAndTime(50, -100);
    assertEquals(1, proxy.cacheStats().evictionCount());
    assertEquals(2, proxy.getCacheSize());
  }
}
