package org.mholford.jacpol;

import java.util.List;
import java.util.Map;

public class Policy {
  private String id;
  private Map<String, Matcher> target;
  private String ruleCombiningAlgorithm;
  private int priority;
  private Map<String, String> obligations;
  private List<Rule> rules;

  public Policy(){}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRuleCombiningAlgorithm() {
    return ruleCombiningAlgorithm;
  }

  public void setRuleCombiningAlgorithm(String ruleCombiningAlgorithm) {
    this.ruleCombiningAlgorithm = ruleCombiningAlgorithm;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public Map<String, String> getObligations() {
    return obligations;
  }

  public void setObligations(Map<String, String> obligations) {
    this.obligations = obligations;
  }

  public List<Rule> getRules() {
    return rules;
  }

  public void setRules(List<Rule> rules) {
    this.rules = rules;
  }

  public Map<String, Matcher> getTarget() {
    return target;
  }

  public void setTarget(Map<String, Matcher> target) {
    this.target = target;
  }
}
