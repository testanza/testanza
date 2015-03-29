package org.quackery.contract.collection;

import static org.quackery.Contracts.quacksLike;
import static org.quackery.contract.collection.Bugs.bugs;
import static org.quackery.contract.collection.Bugs.implementations;
import static org.quackery.testing.Assertions.assertFailure;
import static org.quackery.testing.Assertions.assertSuccess;

import java.util.Collection;
import java.util.List;

import org.quackery.Contract;

public class test_contract_for_list_mutable {
  private final Contract<Class<?>> contract = quacksLike(List.class).mutable();

  public void accepts_mutable_lists() {
    for (Class<?> implementation : implementations(List.class, Mutable.class)) {
      assertSuccess(contract.test(implementation));
    }
  }

  public void detects_collection_bugs() {
    for (Class<?> bug : bugs(Collection.class)) {
      assertFailure(contract.test(bug));
    }
  }

  public void detects_mutable_collection_bugs() {
    for (Class<?> bug : bugs(Collection.class, Mutable.class)) {
      assertFailure(contract.test(bug));
    }
  }

  public void detects_list_bugs() {
    for (Class<?> bug : bugs(List.class)) {
      assertFailure(contract.test(bug));
    }
  }

  public void detects_mutable_list_bugs() {
    for (Class<?> bug : bugs(List.class, Mutable.class)) {
      assertFailure(contract.test(bug));
    }
  }
}
