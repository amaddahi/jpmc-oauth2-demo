package org.mholford.jacpol;

public class Target {
  private Matcher srcDomain;
  private Matcher msgType;

  public Target(){}

  public Matcher getSrcDomain() {
    return srcDomain;
  }

  public void setSrcDomain(Matcher srcDomain) {
    this.srcDomain = srcDomain;
  }

  public Matcher getMsgType() {
    return msgType;
  }

  public void setMsgType(Matcher msgType) {
    this.msgType = msgType;
  }
}
