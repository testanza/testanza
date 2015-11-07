package org.quackery.contract.collection.bug.collection.immutable;

import java.util.Collection;
import java.util.Iterator;

import org.quackery.contract.collection.ImmutableList;

public class IteratorDoesNotThrowException<E> extends ImmutableList<E> {
  public IteratorDoesNotThrowException() {}

  public IteratorDoesNotThrowException(Collection<E> collection) {
    super(collection);
  }

  public Iterator<E> iterator() {
    final Iterator<E> iterator = delegate.iterator();
    return new Iterator<E>() {
      public boolean hasNext() {
        return iterator.hasNext();
      }

      public E next() {
        return iterator.next();
      }

      public void remove() {}
    };
  }
}