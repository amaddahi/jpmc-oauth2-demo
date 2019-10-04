package org.mholford.jacpol;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = MatcherDeserializer.class)
public interface Matcher {
  boolean matches(String val);
  boolean matches(List<String> vals);
}
