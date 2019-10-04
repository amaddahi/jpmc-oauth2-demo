package org.mholford.jacpol;

import java.util.List;

public class EqMatcher implements Matcher {
  private List<String> values;

  public EqMatcher(){}

  public EqMatcher(String value) {
    values = List.of(value);
  }

  public EqMatcher(List<String> values) {
    this.values = values;
  }
  @Override
  public boolean matches(String val) {
    return values.contains(val);
  }

  @Override
  public boolean matches(List<String> vals) {
    return values.containsAll(vals);
  }
}
