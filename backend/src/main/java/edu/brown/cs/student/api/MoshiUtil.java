package edu.brown.cs.student.api;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.api.exceptions.DeserializeException;

import edu.brown.cs.student.api.formats.AlbumRecord;
import edu.brown.cs.student.api.formats.ArtistRecord;
import edu.brown.cs.student.api.formats.SearchRecord;
import edu.brown.cs.student.api.formats.SearchRecord.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import edu.brown.cs.student.api.formats.TrackRecord;
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
   * Deserializes a buffer JSON into a Location record.
   *
   * @param buf A Buffer representing the JSON of the NWS location endpoint.
   * @return A Location record storing this JSON's attributes.
   * @throws DeserializeException If an exception has occurred in moshi's methods.
   */
  public static SearchRecord deserializeSearch(Buffer buf) throws DeserializeException {
    try {
      // make a new moshi adapter and
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<SearchRecord> adapter = moshi.adapter(
          Types.newParameterizedType(SearchRecord.class, Artists.class, ArtistRecord.class, Albums.class, AlbumRecord.class, Tracks.class, TrackRecord.class));
      return adapter.fromJson(buf);
    } catch (Exception e) {
      throw new DeserializeException(e.getMessage());
    }
  }

  public static AlbumRecord deserializeUpdate(Buffer buf) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<AlbumRecord> adapter = moshi.adapter(
            Types.newParameterizedType(AlbumRecord.class));
    return adapter.fromJson(buf);
  }

  public static Map<String, String> deserializeToken(Buffer buf) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapter = moshi.adapter(
        Types.newParameterizedType(Map.class, String.class, String.class));
    return adapter.fromJson(buf);
  }
}
