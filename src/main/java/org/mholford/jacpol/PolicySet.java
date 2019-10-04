package org.mholford.jacpol;

import java.util.List;
import java.util.Map;

public class PolicySet {
  private String id;
  private String version;
  private String update;
  private Map<String, Matcher> target;
  private String policyCombiningAlgorithm;
  private Map<String, String> obligations;
  private int priority;
  private List<Policy> policies;

  public PolicySet(){}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getUpdate() {
    return update;
  }

  public void setUpdate(String update) {
    this.update = update;
  }

  public String getPolicyCombiningAlgorithm() {
    return policyCombiningAlgorithm;
  }

  public void setPolicyCombiningAlgorithm(String policyCombiningAlgorithm) {
    this.policyCombiningAlgorithm = policyCombiningAlgorithm;
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

  public List<Policy> getPolicies() {
    return policies;
  }

  public void setPolicies(List<Policy> policies) {
    this.policies = policies;
  }

  public Map<String, Matcher> getTarget() {
    return target;
  }

  public void setTarget(Map<String, Matcher> target) {
    this.target = target;
  }
}
