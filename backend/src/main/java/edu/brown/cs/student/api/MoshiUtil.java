package edu.brown.cs.student.api;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.api.exceptions.DeserializeException;
import edu.brown.cs.student.api.formats.*;
import edu.brown.cs.student.api.formats.SearchRecord.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import okio.Buffer;

/** Util class for moshi serializing and deserializing. */
public class MoshiUtil {

  /**
   * Serializes a given map with a response code.
   *
   * @param responseMap Map to populate.
   * @param responseCode Code to put in the "result" field.
   * @return String representation of the response map.
   */
  public static String serialize(Map<String, Object> responseMap, String responseCode) {
    responseMap.put("result", responseCode);
    Moshi moshi = new Builder().build();
    Type responseMapType = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(responseMapType);
    return adapter.toJson(responseMap);
  }

  /**
   * Deserializes a buffer JSON into a SearchRecord.
   *
   * @param buf A Buffer representing the JSON of the Search results
   * @return A SearchRecord containing hte search results as objects
   * @throws DeserializeException If an exception has occurred in moshi's methods.
   */
  public static SearchRecord deserializeSearch(Buffer buf) throws DeserializeException {
    try {
      // make a new moshi adapter and
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<SearchRecord> adapter =
          moshi.adapter(
              Types.newParameterizedType(
                  SearchRecord.class,
                  Artists.class,
                  ArtistRecord.class,
                  Albums.class,
                  AlbumRecord.class,
                  Tracks.class,
                  TrackRecord.class,
                  ImageRecord.class));
      return adapter.fromJson(buf);
    } catch (Exception e) {
      throw new DeserializeException(e.getMessage());
    }
  }

  /**
   * Deserializes a buffer JSON into a ArtistRecord.
   *
   * @param buf A Buffer representing the JSON of the /get artist endpoitn results
   * @return A SearchRecord containing the artist record json as an object
   * @throws DeserializeException If an exception has occurred in moshi's methods.
   */
  public static ArtistRecord deserializeArtistRecord(Buffer buf) throws DeserializeException {
    try {
      // make a new moshi adapter and
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ArtistRecord> adapter =
          moshi.adapter(Types.newParameterizedType(ArtistRecord.class, ImageRecord.class));
      return adapter.fromJson(buf);
    } catch (Exception e) {
      throw new DeserializeException(e.getMessage());
    }
  }

  public static Albums deserializeUpdate(Buffer buf) throws DeserializeException {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<Albums> adapter =
          moshi.adapter(
              Types.newParameterizedType(
                  Albums.class, List.class, AlbumRecord.class, ImageRecord.class));
      return adapter.fromJson(buf);
    } catch (Exception e) {
      throw new DeserializeException(e.getMessage());
    }
  }

  public static Map<String, String> deserializeToken(Buffer buf) throws DeserializeException {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<Map<String, String>> adapter =
          moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
      return adapter.fromJson(buf);
    } catch (Exception e) {
      throw new DeserializeException(e.getMessage());
    }
  }

  public static ArtistFollowRecord deserializeArtistsFollow(Buffer buf)
      throws DeserializeException {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ArtistFollowRecord> adapter =
          moshi.adapter(
              Types.newParameterizedType(
                  ArtistFollowRecord.class, ArtistFollowRecord.ArtistsJSON.class, Artists.class));
      return adapter.fromJson(buf);
    } catch (Exception e) {
      throw new DeserializeException(e.getMessage());
    }
  }
}
