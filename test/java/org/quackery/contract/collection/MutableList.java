package org.quackery.contract.collection;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.quackery.contract.Bug;

public class MutableList<E> implements Mutable, List<E> {
  protected List<E> delegate;

  public MutableList() {
    delegate = new ArrayList<>();
  }

  public MutableList(Collection<E> collection) {
    delegate = new ArrayList<>(collection);
  }

  public int size() {
    return delegate.size();
  }

  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  public boolean contains(Object o) {
    return delegate.contains(o);
  }

  public Iterator<E> iterator() {
    return delegate.iterator();
  }

  public Object[] toArray() {
    return delegate.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return delegate.toArray(a);
  }

  public boolean add(E e) {
    return delegate.add(e);
  }

  public boolean remove(Object o) {
    return delegate.remove(o);
  }

  public boolean containsAll(Collection<?> c) {
    return delegate.containsAll(c);
  }

  public boolean addAll(Collection<? extends E> c) {
    return delegate.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends E> c) {
    return delegate.addAll(index, c);
  }

  public boolean removeAll(Collection<?> c) {
    return delegate.removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return delegate.retainAll(c);
  }

  public void clear() {
    delegate.clear();
  }

  public E get(int index) {
    return delegate.get(index);
  }

  public E set(int index, E element) {
    return delegate.set(index, element);
  }

  public void add(int index, E element) {
    delegate.add(index, element);
  }

  public E remove(int index) {
    return delegate.remove(index);
  }

  public int indexOf(Object o) {
    return delegate.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return delegate.lastIndexOf(o);
  }

  public ListIterator<E> listIterator() {
    return delegate.listIterator();
  }

  public ListIterator<E> listIterator(int index) {
    return delegate.listIterator(index);
  }

  public List<E> subList(int fromIndex, int toIndex) {
    return delegate.subList(fromIndex, toIndex);
  }

  public boolean equals(Object o) {
    return delegate.equals(o);
  }

  public int hashCode() {
    return delegate.hashCode();
  }

  public String toString() {
    return delegate.toString();
  }

  @Bug(Collection.class)
  public static class DefaultConstructorIsMissing<E> extends MutableList<E> {
    public DefaultConstructorIsMissing(Collection<E> collection) {
      super(collection);
    }
  }

  @Bug(Collection.class)
  public static class DefaultConstructorIsHidden<E> extends MutableList<E> {
    DefaultConstructorIsHidden() {}

    public DefaultConstructorIsHidden(Collection<E> collection) {
      super(collection);
    }
  }

  @Bug(Collection.class)
  public static class DefaultConstructorAddsElement<E> extends MutableList<E> {
    public DefaultConstructorAddsElement() {
      super(asList((E) newObject("x")));
    }

    public DefaultConstructorAddsElement(Collection<E> collection) {
      super(collection);
    }
  }

  @Bug(Collection.class)
  public static class CopyConstructorIsMissing<E> extends MutableList<E> {
    public CopyConstructorIsMissing() {}
  }

  @Bug(Collection.class)
  public static class CopyConstructorIsHidden<E> extends MutableList<E> {
    public CopyConstructorIsHidden() {}

    CopyConstructorIsHidden(Collection<E> collection) {
      super(collection);
    }
  }

  @Bug(Collection.class)
  public static class CopyConstructorCreatesEmpty<E> extends MutableList<E> {
    public CopyConstructorCreatesEmpty() {}

    public CopyConstructorCreatesEmpty(Collection<E> collection) {
      if (collection == null) {
        throw new NullPointerException();
      }
    }
  }

  @Bug(Collection.class)
  public static class CopyConstructorAddsElement<E> extends MutableList<E> {
    public CopyConstructorAddsElement() {}

    public CopyConstructorAddsElement(Collection<E> collection) {
      super(add((E) newObject("x"), collection));
    }

    private static <E> Collection<E> add(E element, Collection<E> collection) {
      List<E> added = new ArrayList<>(collection);
      added.add(element);
      return added;
    }
  }

  @Bug(Collection.class)
  public static class CopyConstructorAcceptsNull<E> extends MutableList<E> {
    public CopyConstructorAcceptsNull() {}

    public CopyConstructorAcceptsNull(Collection<E> collection) {
      super(unnull(collection));
    }

    private static <E> Collection<E> unnull(Collection<E> collection) {
      return collection == null
          ? Arrays.<E> asList()
          : collection;
    }
  }

  @Bug(Collection.class)
  public static class CopyConstructorMakesNoDefensiveCopy<E> extends MutableList<E> {
    public CopyConstructorMakesNoDefensiveCopy() {}

    public CopyConstructorMakesNoDefensiveCopy(Collection<E> collection) {
      super(collection);
      delegate = (List<E>) collection;
    }
  }

  @Bug(Collection.class)
  public static class CopyConstructorModifiesArgument<E> extends MutableList<E> {
    public CopyConstructorModifiesArgument() {}

    public CopyConstructorModifiesArgument(Collection<E> collection) {
      super(collection);
      collection.add((E) newObject("x"));
    }
  }

  @Bug(Collection.class)
  public static class CopyConstructorCreatesFixed<E> extends MutableList<E> {
    public CopyConstructorCreatesFixed() {}

    public CopyConstructorCreatesFixed(Collection<E> collection) {
      super(asList((E) newObject("x")));
      if (collection == null) {
        throw new NullPointerException();
      }
    }
  }

  @Bug(List.class)
  public static class CopyConstructorStoresOneElement<E> extends MutableList<E> {
    public CopyConstructorStoresOneElement() {}

    public CopyConstructorStoresOneElement(Collection<E> collection) {
      super(one(collection));
    }

    private static <E> Collection<E> one(Collection<E> collection) {
      return collection.isEmpty()
          ? collection
          : asList(collection.iterator().next());
    }
  }

  @Bug(List.class)
  public static class CopyConstructorReversesOrder<E> extends MutableList<E> {
    public CopyConstructorReversesOrder() {}

    public CopyConstructorReversesOrder(Collection<E> collection) {
      super(reverse(collection));
    }

    private static <E> Collection<E> reverse(Collection<E> collection) {
      List<E> list = new ArrayList<>(collection);
      Collections.reverse(list);
      return list;
    }
  }

  @Bug(List.class)
  public static class CopyConstructorRemovesLastElement<E> extends MutableList<E> {
    public CopyConstructorRemovesLastElement() {}

    public CopyConstructorRemovesLastElement(Collection<E> collection) {
      super(withoutLast(collection));
    }

    private static <E> Collection<E> withoutLast(Collection<E> collection) {
      List<E> list = new ArrayList<>(collection);
      if (!list.isEmpty()) {
        list.remove(list.size() - 1);
      }
      return list;
    }
  }

  @Bug(Collection.class)
  public static class ToArrayReturnsEmpty<E> extends MutableList<E> {
    public ToArrayReturnsEmpty() {}

    public ToArrayReturnsEmpty(Collection<E> collection) {
      super(collection);
    }

    public Object[] toArray() {
      return new Object[0];
    }
  }

  @Bug(Collection.class)
  public static class ToArrayReturnsUnknownElement<E> extends MutableList<E> {
    public ToArrayReturnsUnknownElement() {}

    public ToArrayReturnsUnknownElement(Collection<E> collection) {
      super(collection);
    }

    public Object[] toArray() {
      return new Object[] { newObject("x") };
    }
  }

  @Bug(Collection.class)
  public static class ToArrayReturnsNull<E> extends MutableList<E> {
    public ToArrayReturnsNull() {}

    public ToArrayReturnsNull(Collection<E> collection) {
      super(collection);
    }

    public Object[] toArray() {
      return null;
    }
  }

  @Bug(Collection.class)
  public static class SizeReturnsZero<E> extends MutableList<E> {
    public SizeReturnsZero() {}

    public SizeReturnsZero(Collection<E> collection) {
      super(collection);
    }

    public int size() {
      return 0;
    }
  }

  @Bug(Collection.class)
  public static class SizeReturnsOne<E> extends MutableList<E> {
    public SizeReturnsOne() {}

    public SizeReturnsOne(Collection<E> collection) {
      super(collection);
    }

    public int size() {
      return 1;
    }
  }

  @Bug(Collection.class)
  public static class IsEmptyReturnsTrue<E> extends MutableList<E> {
    public IsEmptyReturnsTrue() {}

    public IsEmptyReturnsTrue(Collection<E> collection) {
      super(collection);
    }

    public boolean isEmpty() {
      return true;
    }
  }

  @Bug(Collection.class)
  public static class IsEmptyReturnsFalse<E> extends MutableList<E> {
    public IsEmptyReturnsFalse() {}

    public IsEmptyReturnsFalse(Collection<E> collection) {
      super(collection);
    }

    public boolean isEmpty() {
      return false;
    }
  }

  @Bug(Collection.class)
  public static class IsEmptyNegates<E> extends MutableList<E> {
    public IsEmptyNegates() {}

    public IsEmptyNegates(Collection<E> collection) {
      super(collection);
    }

    public boolean isEmpty() {
      return !super.isEmpty();
    }
  }

  @Bug(List.class)
  public static class GetReturnsFirstElement<E> extends MutableList<E> {
    public GetReturnsFirstElement() {}

    public GetReturnsFirstElement(Collection<E> collection) {
      super(collection);
    }

    public E get(int index) {
      return super.get(0);
    }
  }

  @Bug(List.class)
  public static class GetReturnsLastElement<E> extends MutableList<E> {
    public GetReturnsLastElement() {}

    public GetReturnsLastElement(Collection<E> collection) {
      super(collection);
    }

    public E get(int index) {
      return super.get(size() - 1);
    }
  }

  @Bug(List.class)
  public static class GetReturnsNull<E> extends MutableList<E> {
    public GetReturnsNull() {}

    public GetReturnsNull(Collection<E> collection) {
      super(collection);
    }

    public E get(int index) {
      return null;
    }
  }

  @Bug(List.class)
  public static class GetReturnsNullAboveBound<E> extends MutableList<E> {
    public GetReturnsNullAboveBound() {}

    public GetReturnsNullAboveBound(Collection<E> collection) {
      super(collection);
    }

    public E get(int index) {
      return index >= size()
          ? null
          : super.get(index);
    }
  }

  @Bug(List.class)
  public static class GetReturnsNullBelowBound<E> extends MutableList<E> {
    public GetReturnsNullBelowBound() {}

    public GetReturnsNullBelowBound(Collection<E> collection) {
      super(collection);
    }

    public E get(int index) {
      return index < 0
          ? null
          : super.get(index);
    }
  }

  @Bug({ Collection.class, Mutable.class })
  public static class ClearHasNoEffect<E> extends MutableList<E> {
    public ClearHasNoEffect() {}

    public ClearHasNoEffect(Collection<E> collection) {
      super(collection);
    }

    public void clear() {}
  }

  @Bug({ List.class, Mutable.class })
  public static class AddHasNoEffect<E> extends MutableList<E> {
    public AddHasNoEffect() {}

    public AddHasNoEffect(Collection<E> collection) {
      super(collection);
    }

    public boolean add(E e) {
      return true;
    }
  }

  @Bug({ List.class, Mutable.class })
  public static class AddAddsAtTheBegin<E> extends MutableList<E> {
    public AddAddsAtTheBegin() {}

    public AddAddsAtTheBegin(Collection<E> collection) {
      super(collection);
    }

    public boolean add(E e) {
      super.add(0, e);
      return true;
    }
  }

  @Bug({ List.class, Mutable.class })
  public static class AddReturnsFalse<E> extends MutableList<E> {
    public AddReturnsFalse() {}

    public AddReturnsFalse(Collection<E> collection) {
      super(collection);
    }

    public boolean add(E e) {
      super.add(e);
      return false;
    }
  }

  private static Object newObject(final String name) {
    return new Object() {
      public String toString() {
        return name;
      }
    };
  }
}
