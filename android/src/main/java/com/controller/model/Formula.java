package com.controller.model;

public class Formula implements Comparable {

  private int start;
  private int duration;
  private int cartridge;
  private int value;

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getCartridge() {
    return cartridge;
  }

  public void setCartridge(int cartridge) {
    this.cartridge = cartridge;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public int compareTo(Object o) {
    Formula item = (Formula) o;
    return Integer.compare(this.getStart(), item.getStart());

  }

  @Override
  public String toString() {
    return "Formula{" +
      "start=" + start +
      ", duration=" + duration +
      ", cartridge=" + cartridge +
      ", value=" + value +
      '}';
  }
}

