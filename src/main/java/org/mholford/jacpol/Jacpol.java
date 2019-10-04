package org.mholford.jacpol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.representations.AccessToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Jacpol {
  private static Jacpol INSTANCE;

  private Jacpol() {
  }

  public static Jacpol get() {
    if (INSTANCE == null) {
      INSTANCE = new Jacpol();
    }
    return INSTANCE;
  }

  public PolicySet getPolicySet(String filename) throws IOException {
    ObjectMapper om = new ObjectMapper();
    PolicySet policySet = om.readValue(new File(filename), PolicySet.class);
    return policySet;
  }

  public List<String> getNeoRoles(PolicySet policySet, AccessToken token) {
    List<String> output = new ArrayList<>();
    for (Policy policy : policySet.getPolicies()) {
      for (Rule rule : policy.getRules()) {
        if (matchTargets(rule.getTarget(), token)) {
          if (matchCondition(rule, token)) {
            output.add(getNeoRoleMapping(rule));
          }
        }
      }
    }
    return output;
  }

  public boolean matchTargets(List<Map<String, Matcher>> targets, AccessToken token) {
    boolean matches = true;
    Iterator<Map<String, Matcher>> iter = targets.iterator();
    do {
      matches = matchTarget(iter.next(), token);
    } while (iter.hasNext() && matches);
    return matches;
  }

  public boolean matchTarget(Map<String, Matcher> target, AccessToken token) {
    for (Map.Entry<String, Matcher> e : target.entrySet()) {
      switch (e.getKey()) {
        case "msgType":
          String msgType = token.getType();
          return e.getValue().matches(msgType);
        case "srcDomain":
          String srcDomain = token.getIssuedFor();
          return e.getValue().matches(srcDomain);
        default:
          return false;
      }
    }
    return false;
  }

  public boolean matchCondition(Rule rule, AccessToken token) {
    Map<String, Matcher> condition = rule.getCondition();

    // Do we need something more dynamic here?
    if (condition.containsKey("realmAccessRole")) {
      return condition.get("realmAccessRole").matches(new ArrayList<>(token.getRealmAccess().getRoles()));
    }
    return false;
  }

  public String getNeoRoleMapping(Rule rule) {
    if (rule.getObligations().containsKey("mapToNeoRole")) {
      return rule.getObligations().get("mapToNeoRole");
    } else {
      return null;
    }
  }
}
