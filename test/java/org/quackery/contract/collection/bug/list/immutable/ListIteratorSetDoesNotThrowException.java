package org.quackery.contract.collection.bug.list.immutable;

import java.util.Collection;
import java.util.ListIterator;

import org.quackery.contract.collection.ImmutableList;

public class ListIteratorSetDoesNotThrowException<E> extends ImmutableList<E> {
  public ListIteratorSetDoesNotThrowException() {}

  public ListIteratorSetDoesNotThrowException(Collection<E> collection) {
    super(collection);
  }

  public ListIterator<E> listIterator() {
    return new UnmodifiableIterator(delegate.listIterator()) {
      public void set(Object e) {}
    };
  }
}
