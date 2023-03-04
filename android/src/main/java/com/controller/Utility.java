package com.controller;

import com.aromajoin.sdk.core.device.Port;
import com.facebook.react.bridge.ReadableArray;

public class Utility {
  public static int[] convertToIntArray(ReadableArray list) {
    int[] arr = new int[list.size()];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = list.getInt(i);
    }
    return arr;
  }

  /**
   * 转化为port 数组
   *
   * @param portTemplate 格式：portNumber|intensity,portNumber|intensity
   * @return
   */
  public static Port[] convertToPort(String portTemplate) throws Exception {
    String[] split = portTemplate.split(",");
    Port[] ports = new Port[split.length];
    for (int i = 0; i < split.length; i++) {
      String[] portParams = split[0].split("\\|");
      int portNumber = Integer.parseInt(portParams[0]);
      int intensity = Integer.parseInt(portParams[1]);
      if (portNumber > 6) throw new Exception(i + "参数错误" + portNumber + intensity);
      intensity = Math.min(intensity, 100);
      ports[i] = new Port(portNumber, intensity);
    }
    return ports;
  }
}
