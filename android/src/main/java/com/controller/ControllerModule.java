package com.controller;


import android.bluetooth.BluetoothAdapter;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.aromajoin.sdk.android.ble.AndroidBLEController;
import com.aromajoin.sdk.core.callback.ConnectCallback;
import com.aromajoin.sdk.core.device.AromaShooter;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ReactModule(name = ControllerModule.NAME)
public class ControllerModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Controller";
  private final int DEFAULT_DURATION = 3000; // Unit: millisecond

  private ReactApplicationContext reactContext;

  private Map<String, AromaShooter> aromaShooterMap = new HashMap<>();


  public ControllerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  @ReactMethod
  public void checkBluetooth(Promise promise) {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter == null) {
      promise.resolve(-1);
      return;
    }
    promise.resolve(bluetoothAdapter.isEnabled() ? 1 : 0);
  }


  @ReactMethod
  public void autoOpenBluetooth(Promise promise) {
    promise.resolve(1);
  }

  @ReactMethod
  public void deviceList(Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();

    bleController.startScan(reactContext, aromaShooters -> {
      if (aromaShooters != null && aromaShooters.size() > 0) {
        aromaShooters.forEach(item -> aromaShooterMap.put(item.getSerial(), item));
        promise.resolve(JSON.toJSONString(aromaShooters.stream().map(AromaShooter::getSerial).collect(Collectors.toList())));
      }
    });
  }


  @ReactMethod
  public void deviceCacheList(Promise promise) {
    promise.resolve(JSON.toJSONString(aromaShooterMap.values().stream().map(AromaShooter::getSerial).collect(Collectors.toList())));
  }


  @ReactMethod
  public void connectDevice(String serial, Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();

    AromaShooter aromaShooter = aromaShooterMap.get(serial);
    if (aromaShooter == null) {
      promise.resolve(-1);
      return;
    }
    bleController.stopScan(reactContext);
    bleController.connect(aromaShooter, new ConnectCallback() {
      @Override
      public void onConnected(AromaShooter aromaShooter) {
        promise.resolve(1);
      }

      @Override
      public void onFailed(AromaShooter aromaShooter, String msg) {
        promise.resolve(0);

      }
    });

  }


  @ReactMethod
  public void play(Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();

    List<AromaShooter> aromaShooters = bleController.getConnectedDevices();
    if (aromaShooters == null || aromaShooters.size() == 0) {
      promise.resolve(-1);
      return;
    }
    int[] arr = {1};
    bleController.diffuseAll(DEFAULT_DURATION, true, arr);
    promise.resolve(1);
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void diffuse(ReadableArray ports, Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();
    List<AromaShooter> aromaShooters = bleController.getConnectedDevices();
    if (aromaShooters == null || aromaShooters.size() == 0) { // check whether there is any connected devices.
      promise.reject(new Exception("No connected devices"));
      return;
    }
    bleController.diffuseAll(DEFAULT_DURATION, true, Utility.convertToIntArray(ports));
    promise.resolve(null);
  }
}
