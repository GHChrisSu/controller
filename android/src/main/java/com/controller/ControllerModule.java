package com.controller;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.aromajoin.sdk.android.ble.AndroidBLEController;
import com.aromajoin.sdk.android.ble.ui.ASBaseActivity;
import com.aromajoin.sdk.core.device.AromaShooter;

import java.util.List;

@ReactModule(name = ControllerModule.NAME)
public class ControllerModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Controller";
  private final int DEFAULT_DURATION = 3000; // Unit: millisecond

  public ControllerModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void diffuse(ReadableArray ports, Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();
    List<AromaShooter> aromaShooters = bleController.getConnectedDevices();
    if (aromaShooters == null
      || aromaShooters.size() == 0) { // check whether there is any connected devices.
      promise.reject(new Exception("No connected devices"));
      return;
    }
    bleController.diffuseAll(DEFAULT_DURATION, true, Utility.convertToIntArray(ports));
    promise.resolve(null);
  }
}
