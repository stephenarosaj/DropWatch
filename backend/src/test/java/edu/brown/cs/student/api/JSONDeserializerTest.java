package edu.brown.cs.student.api;

import static org.testng.AssertJUnit.assertTrue;

import edu.brown.cs.student.api.weatherHelpers.format.Pets;
import edu.brown.cs.student.api.weatherHelpers.format.Pets.PetTypes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import okio.Buffer;
import org.junit.jupiter.api.Test;

public class JSONDeserializerTest {

  public Buffer mockToBuffer(String mockFile) throws IOException {
    String mockedString = new String(Files.readAllBytes(Paths.get(mockFile)));
    return new Buffer().readFrom(new ByteArrayInputStream(mockedString.getBytes()));
  }

  @Test
  public void testNonNestedFields() throws IOException {
    JSONDeserializer<Pets> genericDeserializer =
        new JSONDeserializer(Pets.class, new Class[] {PetTypes.class});
    Buffer buf = mockToBuffer("data/mockedData/petJSON.json");
    Pets pets = genericDeserializer.deserialize(buf);

    List<String> henryExpected = List.of("fido", "spot");
    List<String> camilaExpected = List.of("waggy", "barker");
    List<String> richardExpected = List.of("doggy", "doggy_2");

    assertTrue(pets.CSVName().equals("pets"));
    assertTrue(pets.henryPets().equals(henryExpected));
    assertTrue(pets.camilaPets().equals(camilaExpected));
    assertTrue(pets.richardPets().equals(richardExpected));
  }

  @Test
  public void testNestedField() throws IOException {
    JSONDeserializer<Pets> genericDeserializer =
        new JSONDeserializer(Pets.class, new Class[] {PetTypes.class});
    Buffer buf = mockToBuffer("data/mockedData/petJSON.json");
    Pets pets = genericDeserializer.deserialize(buf);
    List<String> dogBreedExpected = List.of("beagle", "german_shepherd");
    List<String> catBreedExpected = List.of("maine_coon", "tabby");
    assertTrue(pets.petTypes().dogBreeds().equals(dogBreedExpected));
    assertTrue(pets.petTypes().catBreeds().equals(catBreedExpected));
  }
}
