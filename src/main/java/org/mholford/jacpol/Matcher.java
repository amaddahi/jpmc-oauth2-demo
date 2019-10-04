package org.mholford.jacpol;

import java.util.List;

public interface Matcher {
  boolean matches(String val);
  boolean matches(List<String> vals);
}
