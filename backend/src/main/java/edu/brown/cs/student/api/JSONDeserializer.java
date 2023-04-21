package edu.brown.cs.student.api;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import okio.Buffer;

/** A class for deserializing any generic JSON object. */
public class JSONDeserializer<T> {
  private JsonAdapter<T> adapter;

  public JSONDeserializer(Type parentClass) {
    Moshi moshi = new Moshi.Builder().build();
    Type t = Types.newParameterizedType(parentClass);
    adapter = moshi.adapter(Types.newParameterizedType(parentClass));
  }

  public JSONDeserializer(Type parentClass, Type[] nestedClasses) {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(Types.newParameterizedType(parentClass, nestedClasses));
  }

  public T deserialize(Buffer JSONbuf) throws IOException {
    return adapter.fromJson(JSONbuf);
  }
}
