package com.arextest.diff.model.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trace implements Serializable, Cloneable {

  private List<List<NodeEntity>> currentTraceLeft;
  private List<List<NodeEntity>> currentTraceRight;

  public Trace() {
  }

  public Trace(List<List<NodeEntity>> currentTraceLeft, List<List<NodeEntity>> currentTraceRight) {
    this.currentTraceLeft = new ArrayList<>(currentTraceLeft);
    this.currentTraceRight = new ArrayList<>(currentTraceRight);
  }

  public List<List<NodeEntity>> getCurrentTraceLeft() {
    return currentTraceLeft;
  }

  public void setCurrentTraceLeft(List<List<NodeEntity>> currentTraceLeft) {
    this.currentTraceLeft = currentTraceLeft;
  }

  public List<List<NodeEntity>> getCurrentTraceRight() {
    return currentTraceRight;
  }

  public void setCurrentTraceRight(List<List<NodeEntity>> currentTraceRight) {
    this.currentTraceRight = currentTraceRight;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    Trace trace = ((Trace) super.clone());
    trace.currentTraceLeft = this.currentTraceLeft;
    return super.clone();
  }
}