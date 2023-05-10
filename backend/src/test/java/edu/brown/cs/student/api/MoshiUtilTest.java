package edu.brown.cs.student.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import edu.brown.cs.student.api.exceptions.DeserializeException;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.student.api.formats.SearchRecord;
import okio.Buffer;
import org.junit.jupiter.api.Test;

public class MoshiUtilTest {
  @Test
  public void testForecastDeserialize() throws IOException, DeserializeException {
      String mockedString = Files.readString(Path.of("data/mockedData/mockSpotifyAPISearchResponse.json"));

    Buffer mockedBuffer = new Buffer().readFrom(new ByteArrayInputStream(mockedString.getBytes()));
    SearchRecord searchRecord = MoshiUtil.deserializeSearch(mockedBuffer);
    assertTrue(searchRecord.artists().items().get(0).name().equals("Doja Cat"));
    assertTrue(searchRecord.tracks().total().equals(1000));
  }
//
//  /**
//   * Tests that the DeserializeException is thrown if an error occurs while deserializing.
//   *
//   * @throws IOException
//   */
  @Test
  public void testThrowsDeserializeException() throws IOException {
    String mockedString = "{bleep bloop nonsensical json}";
    Buffer mockedBuffer = new Buffer().readFrom(new ByteArrayInputStream(mockedString.getBytes()));

    assertThrows(
        DeserializeException.class,
        () -> {
          MoshiUtil.deserializeSearch(mockedBuffer);
        });

    assertThrows(
        DeserializeException.class,
        () -> {
          MoshiUtil.deserializeArtistRecord(mockedBuffer);
        });

      assertThrows(
              DeserializeException.class,
              () -> {
                  MoshiUtil.deserializeUpdate(mockedBuffer);
              });

      assertThrows(
              DeserializeException.class,
              () -> {
                  MoshiUtil.deserializeArtistRecord(mockedBuffer);
              });
  }
}
