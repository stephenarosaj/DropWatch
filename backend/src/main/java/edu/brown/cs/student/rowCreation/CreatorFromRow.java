package edu.brown.cs.student.rowCreation;

import java.util.List;

/**
 * A generic interface that allows classes that implement it to create an Object from a list of
 * strings passed in.
 *
 * @param <T> The Object that is created from whatever row of strings is passed in.
 */
public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;
}
