package com.controller;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.aromajoin.sdk.android.ble.AndroidBLEController;
import com.aromajoin.sdk.core.callback.ConnectCallback;
import com.aromajoin.sdk.core.callback.DisconnectCallback;
import com.aromajoin.sdk.core.device.AromaShooter;
import com.aromajoin.sdk.core.device.Port;
import com.controller.model.Formula;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@ReactModule(name = ControllerModule.NAME)
public class ControllerModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Controller";
  private final int DEFAULT_DURATION = 3000; // Unit: millisecond

  private final ReactApplicationContext reactContext;

  private final Map<String, AromaShooter> aromaShooterMap = new HashMap<>();

  private static final int DEFAULT_BOO_INTENSITY = 50;
  private static final int DEFAULT_FAN_INTENSITY = 50;

  private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1;

  private Promise permissionPromise;

  private final static List<Formula> currentPortFormulas = new ArrayList<>();


  public static AtomicBoolean play = new AtomicBoolean(false);

  public static int currentTime = 0;

  public static int totalTime;

  private static ScheduledExecutorService scheduledExecutor;

  private ScheduledFuture<?> scheduledFuture;

  public ControllerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    scheduledExecutor = Executors.newScheduledThreadPool(2);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  /**
   * 检查当前手机的蓝牙是否可用
   *
   * @param promise
   */
  @ReactMethod
  public void checkBluetooth(Promise promise) {
    try {
      BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if (bluetoothAdapter == null) {
        promise.resolve(-1);
        return;
      }
      if (bluetoothAdapter.isEnabled()) {
        promise.resolve(1);
        return;
      }
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      getReactApplicationContext().startActivity(enableBtIntent);
      promise.resolve(1);
    } catch (SecurityException exception) {
      promise.reject("ERROR", "请允许蓝牙权限");
    }
  }


  @ReactMethod
  public void checkPermission(Promise promise) {
    if (ContextCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      promise.resolve(1);
      return;
    }
    this.permissionPromise = promise;
    ActivityCompat.requestPermissions(Objects.requireNonNull(getCurrentActivity()), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
  }


  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
      permissionPromise.resolve(1);
    } else {
      permissionPromise.resolve(-1);
    }
  }


  /**
   * 只需要调用一次，让sdk发起扫描可用设备
   *
   * @param promise
   */
  @ReactMethod
  public void deviceList(Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();
    try {
      scheduledExecutor.execute(() -> bleController.startScan(reactContext, aromaShooters -> {
        if (aromaShooters != null && aromaShooters.size() > 0) {
          aromaShooters.forEach(item -> aromaShooterMap.put(item.getSerial(), item));
          promise.resolve(JSON.toJSONString(aromaShooters.stream().map(AromaShooter::getSerial).collect(Collectors.toList())));
        }
      }));
    } catch (SecurityException exception) {
      promise.resolve(-1);
    }


  }


  /**
   * 在获取可用的设备列表时，只需要调用缓存方法，sdk会一直扫描可用设备
   *
   * @param promise 回调
   */
  @ReactMethod
  public void deviceCacheList(Promise promise) {
    promise.resolve(JSON.toJSONString(aromaShooterMap.values().stream().map(AromaShooter::getSerial).collect(Collectors.toList())));
  }


  @ReactMethod
  public void clearDeviceCacheList(Promise promise) {
    aromaShooterMap.clear();
    promise.resolve(1);
  }

  @ReactMethod
  public void stopScan(Promise promise) {
    try {
      AndroidBLEController bleController = AndroidBLEController.getInstance();
      bleController.stopScan(reactContext);
      promise.resolve(1);
    } catch (Exception exception) {
      promise.resolve(-1);
    }
  }

  /**
   * 链接设备
   *
   * @param serial  设备号
   * @param promise 回调
   *                在调用链接设备时属于耗时任务，需要其他线程中执行
   */
  @ReactMethod
  public void connectDevice(String serial, Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();

    AromaShooter aromaShooter = aromaShooterMap.get(serial);
    if (aromaShooter == null) {
      promise.resolve(-1);
      return;
    }
    bleController.stopScan(reactContext);

    scheduledExecutor.execute(() -> bleController.connect(aromaShooter, new ConnectCallback() {
      @Override
      public void onConnected(AromaShooter aromaShooter1) {
        promise.resolve(1);
      }

      @Override
      public void onFailed(AromaShooter aromaShooter1, String msg) {
        promise.resolve(0);

      }
    }));


  }

  /**
   * 断开链接
   *
   * @param serial  设备号码
   * @param promise 回调
   */
  @ReactMethod
  public void disConnectDevice(String serial, Promise promise) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();

    AromaShooter aromaShooter = aromaShooterMap.get(serial);
    if (aromaShooter == null) {
      promise.resolve(-1);
      return;
    }
    bleController.stopScan(reactContext);

    scheduledExecutor.execute(() -> bleController.disconnect(aromaShooter, new DisconnectCallback() {
      @Override
      public void onDisconnect(AromaShooter aromaShooter1) {
        promise.resolve(1);
      }

      @Override
      public void onFailed(AromaShooter aromaShooter1, String msg) {
        promise.resolve(0);

      }
    }));


  }


  /**
   * 提交配方
   *
   * @param formulaJson 配方json
   */
  @ReactMethod
  public void submitFormula(String formulaJson) {
    AndroidBLEController bleController = AndroidBLEController.getInstance();
    currentPortFormulas.clear();
    currentPortFormulas.addAll(JSON.parseArray(formulaJson, Formula.class));
    for (Formula formula : currentPortFormulas) {
      int v = formula.getDuration() + formula.getStart();
      totalTime = Math.max(v, totalTime);
    }
    if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
      return;
    }
    currentTime = 0;
    scheduledFuture = scheduledExecutor.scheduleAtFixedRate(() -> {
      try {
        if (!play.get()) return;
        List<Formula> choiceFormulas = currentPortFormulas.stream().filter(item -> item.getStart() == currentTime).collect(Collectors.toList());
        for (Formula item : choiceFormulas) {
          currentPortFormulas.remove(item);
          Port[] ports = Utility.convertToPort(item.getCartridge() + "|" + item.getValue());
          int fanIntensity = item.getFan();
          if (fanIntensity == 0) fanIntensity = DEFAULT_FAN_INTENSITY;
          if (fanIntensity > 100) fanIntensity = 100;
          bleController.diffuseAll(item.getDuration(), DEFAULT_BOO_INTENSITY, fanIntensity, ports);
          Log.i("submitFormula", "开始执行任务" + item + "剩余任务数量" + currentPortFormulas.size());
        }
        currentTime += 50;
        if (currentTime > totalTime) {
          currentTime = totalTime;
          play.set(false);
          Log.i("submitFormula", "配方播放完成");
          if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
          }
        }
      } catch (Exception e) {
        Log.e("submitFormula", "submitFormula: " + e.getMessage());
      }
    }, 0, 50, TimeUnit.MILLISECONDS);

  }


  /**
   * 播放配方
   *
   * @param promise
   */
  @ReactMethod
  public void playFormula(Promise promise) {
    if (currentTime >= totalTime) {
      promise.resolve(-1);
      return;
    }
    play.set(true);
    promise.resolve(currentTime);
  }

  /**
   * 停止配方播放
   *
   * @param promise
   */
  @ReactMethod
  public void stopFormula(Promise promise) {
    if (currentTime >= totalTime) {
      promise.resolve(-1);
      return;
    }
    play.set(false);
    promise.resolve(currentTime);
  }

  @ReactMethod
  public void resetPlay(Promise promise) {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    currentTime = 0;
  }

  @ReactMethod
  public void getFormulaTime(Promise promise) {
    promise.resolve(currentTime);
  }


  /**
   * 开始喷射香水
   *
   * @param duration         持续时间
   * @param boosterIntensity 喷射强度
   * @param fanIntensity     风扇强度
   * @param portTemplate     格式：portNumber|intensity,portNumber|intensity
   * @param promise          回调
   */
  @ReactMethod
  public void play(int duration, int boosterIntensity, int fanIntensity, String portTemplate, Promise promise) {
    try {
      AndroidBLEController bleController = AndroidBLEController.getInstance();
      List<AromaShooter> aromaShooters = bleController.getConnectedDevices();
      if (aromaShooters == null || aromaShooters.size() == 0) {
        promise.resolve(-1);
        return;
      }
      Port[] ports = Utility.convertToPort(portTemplate);
      if (duration == 0) duration = DEFAULT_DURATION;
      if (duration > 10000) duration = 10000;
      if (boosterIntensity == 0) boosterIntensity = DEFAULT_BOO_INTENSITY;
      if (boosterIntensity > 100) boosterIntensity = 100;
      if (fanIntensity == 0) fanIntensity = DEFAULT_FAN_INTENSITY;
      if (fanIntensity > 100) fanIntensity = 100;
      bleController.diffuseAll(duration, boosterIntensity, fanIntensity, ports);
      promise.resolve(1);
    } catch (Exception exception) {
      promise.resolve(exception.getMessage());
    }
  }


  /**
   * 停止喷射香水
   *
   * @param serial  设备号
   * @param promise 回调
   */
  @ReactMethod
  public void stop(String serial, Promise promise) {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    AndroidBLEController bleController = AndroidBLEController.getInstance();
    AromaShooter aromaShooter = aromaShooterMap.get(serial);
    if (aromaShooter == null) {
      promise.resolve(-1);
      return;
    }
    bleController.stopAllPorts(aromaShooter);
  }


}
