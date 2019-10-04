package org.mholford.jacpol;

import java.util.List;
import java.util.Map;

public class Rule {
  private String id;
  private List<Target> target;
  private Map<String, Matcher> condition;
  private String effect;
  private Map<String, String> obligations;
  private int priority;

  public Rule(){}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Target> getTarget() {
    return target;
  }

  public void setTarget(List<Target> target) {
    this.target = target;
  }

  public Map<String, Matcher> getCondition() {
    return condition;
  }

  public void setCondition(Map<String, Matcher> condition) {
    this.condition = condition;
  }

  public String getEffect() {
    return effect;
  }

  public void setEffect(String effect) {
    this.effect = effect;
  }

  public Map<String, String> getObligations() {
    return obligations;
  }

  public void setObligations(Map<String, String> obligations) {
    this.obligations = obligations;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }
}
