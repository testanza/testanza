package org.quackery.contract.collection;

import java.util.Collection;
import java.util.List;

import org.quackery.contract.Bug;

public class MutableListFactory {
  public static <E> List<E> create(Collection<? extends E> collection) {
    return new MutableList(collection);
  }

  // no constructors
  private MutableListFactory(Object o) {}

  @Bug({ Collection.class, Mutable.class })
  public static class IteratorRemovesHasNoEffect {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.IteratorRemovesHasNoEffect(collection);
    }
  }

  @Bug({ Collection.class, Mutable.class })
  public static class IteratorRemovesSwallowsException {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.IteratorRemovesSwallowsException(collection);
    }
  }

  @Bug({ Collection.class, Mutable.class })
  public static class IteratorRemovesThrowsException {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.IteratorRemovesThrowsException(collection);
    }
  }

  @Bug({ Collection.class, Mutable.class })
  public static class IteratorRemovesThrowsInverted {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.IteratorRemovesThrowsInverted(collection);
    }
  }

  @Bug({ Collection.class, Mutable.class })
  public static class IteratorRemovesIgnoresSecondCall {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.IteratorRemovesIgnoresSecondCall(collection);
    }
  }

  @Bug({ Collection.class, Mutable.class })
  public static class ClearHasNoEffect {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.ClearHasNoEffect(collection);
    }
  }

  @Bug({ List.class, Mutable.class })
  public static class AddHasNoEffect {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.AddHasNoEffect(collection);
    }
  }

  @Bug({ List.class, Mutable.class })
  public static class AddAddsAtTheBegin {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.AddAddsAtTheBegin(collection);
    }
  }

  @Bug({ List.class, Mutable.class })
  public static class AddReturnsFalse {
    public static <E> List<E> create(Collection<? extends E> collection) {
      return new MutableList.AddReturnsFalse(collection);
    }
  }
}
