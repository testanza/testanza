package org.quackery.contract.collection;

import static org.quackery.Suite.suite;
import static org.quackery.contract.collection.Includes.includeIf;
import static org.quackery.contract.collection.Includes.included;
import static org.quackery.contract.collection.suite.CollectionMutableSuite.clearRemovesElement;
import static org.quackery.contract.collection.suite.CollectionSuite.containsReturnsFalseIfCollectionDoesNotContainElement;
import static org.quackery.contract.collection.suite.CollectionSuite.containsReturnsTrueIfCollectionContainsElement;
import static org.quackery.contract.collection.suite.CollectionSuite.copyConstructorIsDeclared;
import static org.quackery.contract.collection.suite.CollectionSuite.copyConstructorIsPublic;
import static org.quackery.contract.collection.suite.CollectionSuite.creatorCanCreateCollectionWithOneElement;
import static org.quackery.contract.collection.suite.CollectionSuite.creatorDoesNotModifyArgument;
import static org.quackery.contract.collection.suite.CollectionSuite.creatorFailsForNullArgument;
import static org.quackery.contract.collection.suite.CollectionSuite.creatorMakesDefensiveCopy;
import static org.quackery.contract.collection.suite.CollectionSuite.defaultConstructorCreatesEmptyCollection;
import static org.quackery.contract.collection.suite.CollectionSuite.defaultConstructorIsDeclared;
import static org.quackery.contract.collection.suite.CollectionSuite.defaultConstructorIsPublic;
import static org.quackery.contract.collection.suite.CollectionSuite.factoryIsDeclared;
import static org.quackery.contract.collection.suite.CollectionSuite.factoryIsPublic;
import static org.quackery.contract.collection.suite.CollectionSuite.factoryIsStatic;
import static org.quackery.contract.collection.suite.CollectionSuite.factoryReturnsCollection;
import static org.quackery.contract.collection.suite.CollectionSuite.implementsCollectionInterface;
import static org.quackery.contract.collection.suite.CollectionSuite.isEmptyReturnsFalseIfCollectionHasOneElement;
import static org.quackery.contract.collection.suite.CollectionSuite.isEmptyReturnsTrueIfCollectionIsEmpty;
import static org.quackery.contract.collection.suite.CollectionSuite.iteratorRemovesElementFromSingletonCollection;
import static org.quackery.contract.collection.suite.CollectionSuite.iteratorRemovesForbidsConsecutiveCalls;
import static org.quackery.contract.collection.suite.CollectionSuite.iteratorRemovesNoElementsFromEmptyCollection;
import static org.quackery.contract.collection.suite.CollectionSuite.iteratorTraversesEmptyCollection;
import static org.quackery.contract.collection.suite.CollectionSuite.iteratorTraversesSingletonCollection;
import static org.quackery.contract.collection.suite.CollectionSuite.sizeReturnsOneIfCollectionHasOneElement;
import static org.quackery.contract.collection.suite.CollectionSuite.sizeReturnsZeroIfCollectionIsEmpty;
import static org.quackery.contract.collection.suite.ListMutableSuite.addAddsElementAtTheEnd;
import static org.quackery.contract.collection.suite.ListMutableSuite.addReturnsTrue;
import static org.quackery.contract.collection.suite.ListSuite.cretorStoresAllElementsInOrder;
import static org.quackery.contract.collection.suite.ListSuite.getFailsForIndexAboveBound;
import static org.quackery.contract.collection.suite.ListSuite.getFailsForIndexBelowBound;
import static org.quackery.contract.collection.suite.ListSuite.getReturnsEachElement;

import java.util.Collection;
import java.util.List;

import org.quackery.Contract;
import org.quackery.Test;

public final class CollectionContract implements Contract<Class<?>> {
  private final Class<?> supertype;
  private final boolean mutable;
  private final String factory;

  public CollectionContract() {
    supertype = Collection.class;
    mutable = false;
    factory = null;
  }

  private CollectionContract(Class<?> supertype, boolean mutable, String factory) {
    this.supertype = supertype;
    this.mutable = mutable;
    this.factory = factory;
  }

  public Test test(Class<?> type) {
    boolean isList = supertype == List.class;
    boolean hasConstructor = factory == null;
    boolean hasFactory = factory != null;
    Creator creator = hasConstructor
        ? new ConstructorCreator(type)
        : new FactoryCreator(type, factory);
    return included(suite(name(type))
        .test(suite("quacks like Collection")
            .test(includeIf(hasConstructor, implementsCollectionInterface(type)))
            .test(includeIf(hasConstructor, suite("provides default constructor")
                .test(defaultConstructorIsDeclared(type))
                .test(defaultConstructorIsPublic(type))
                .test(defaultConstructorCreatesEmptyCollection(type))))
            .test(suite("provides " + name(creator))
                .test(includeIf(hasConstructor, copyConstructorIsDeclared(type)))
                .test(includeIf(hasConstructor, copyConstructorIsPublic(type)))
                .test(includeIf(hasFactory, factoryIsDeclared(type, factory)))
                .test(includeIf(hasFactory, factoryIsPublic(type, factory)))
                .test(includeIf(hasFactory, factoryIsStatic(type, factory)))
                .test(includeIf(hasFactory, factoryReturnsCollection(type, factory)))
                .test(creatorCanCreateCollectionWithOneElement(creator))
                .test(creatorFailsForNullArgument(creator))
                .test(creatorMakesDefensiveCopy(creator))
                .test(creatorDoesNotModifyArgument(creator)))
            .test(suite("overrides size")
                .test(sizeReturnsZeroIfCollectionIsEmpty(creator))
                .test(sizeReturnsOneIfCollectionHasOneElement(creator)))
            .test(suite("overrides isEmpty")
                .test(isEmptyReturnsFalseIfCollectionHasOneElement(creator))
                .test(isEmptyReturnsTrueIfCollectionIsEmpty(creator)))
            .test(suite("overrides contains")
                .test(containsReturnsFalseIfCollectionDoesNotContainElement(creator))
                .test(containsReturnsTrueIfCollectionContainsElement(creator)))
            .test(suite("overrides iterator")
                .test(iteratorTraversesEmptyCollection(creator))
                .test(iteratorTraversesSingletonCollection(creator))
                .test(includeIf(mutable, iteratorRemovesNoElementsFromEmptyCollection(creator)))
                .test(includeIf(mutable, iteratorRemovesElementFromSingletonCollection(creator)))
                .test(includeIf(mutable, iteratorRemovesForbidsConsecutiveCalls(creator)))))
        .test(includeIf(mutable, suite("quacks like mutable collection")
            .test(suite("overrides clear")
                .test(clearRemovesElement(creator)))))
        .test(includeIf(isList, suite("quacks like list")
            .test(suite("provides " + name(creator))
                .test(cretorStoresAllElementsInOrder(creator)))
            .test(suite("overrides get")
                .test(getReturnsEachElement(creator))
                .test(getFailsForIndexAboveBound(creator))
                .test(getFailsForIndexBelowBound(creator)))))
        .test(includeIf(isList && mutable, suite("quacks like mutable list")
            .test(suite("overrides add")
                .test(addAddsElementAtTheEnd(creator))
                .test(addReturnsTrue(creator))))));
  }

  private String name(Class<?> type) {
    StringBuilder builder = new StringBuilder();
    builder.append(type.getName() + " quacks like");
    if (mutable) {
      builder.append(" mutable");
    }
    builder.append(" ").append(supertype.getSimpleName().toLowerCase());
    return builder.toString();
  }

  private static String name(Creator creator) {
    return creator instanceof ConstructorCreator
        ? "copy constructor"
        : "factory method";
  }

  public CollectionContract implementing(Class<?> collectionInterface) {
    return new CollectionContract(collectionInterface, mutable, factory);
  }

  public CollectionContract mutable() {
    return new CollectionContract(supertype, true, factory);
  }

  public CollectionContract withFactory(String factoryMethodName) {
    return new CollectionContract(supertype, mutable, factoryMethodName);
  }
}
