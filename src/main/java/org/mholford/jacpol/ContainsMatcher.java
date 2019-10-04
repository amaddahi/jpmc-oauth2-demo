package org.mholford.jacpol;

import java.util.List;

public class ContainsMatcher implements Matcher {
  private List<String> values;

  public ContainsMatcher(){}
  public ContainsMatcher(List<String> values) {
    this.values = values;
  }
  @Override
  public boolean matches(String val) {
    return values.contains(val);
  }

  @Override
  public boolean matches(List<String> vals) {
    for (String val:vals) {
      if (values.contains(val)) {
        return true;
      }
    }
    return false;
  }
}
