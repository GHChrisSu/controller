package com.controller;

import com.facebook.react.bridge.ReadableArray;

import java.util.List;

public class Utility {
  public static int[] convertToIntArray(ReadableArray list) {
    int[] arr = new int[list.size()];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = list.getInt(i);
    }
    return arr;
  }
}
