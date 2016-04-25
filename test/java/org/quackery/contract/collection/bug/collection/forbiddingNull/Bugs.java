package org.quackery.contract.collection.bug.collection.forbiddingNull;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public class Bugs {
  public static final List<? extends Class<?>> bugs = unmodifiableList(asList(
      org.quackery.contract.collection.bug.collection.forbiddingNull.CreatorAllowsNullElements.class));
}
