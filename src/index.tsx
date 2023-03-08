import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'controller' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Controller = NativeModules.Controller
  ? NativeModules.Controller
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function checkBluetooth(): Promise<any> {
  return Controller.checkBluetooth();
}

export function checkPermission(): Promise<any> {
  return Controller.checkPermission();
}

export function deviceList(): Promise<any> {
  return Controller.deviceList();
}

export function deviceCacheList(): Promise<any> {
  return Controller.deviceCacheList();
}

export function clearDeviceCacheList(): Promise<any> {
  return Controller.clearDeviceCacheList();
}

export function connectDevice(serial: string): Promise<any> {
  return Controller.connectDevice(serial);
}

export function disConnectDevice(serial: string): Promise<any> {
  return Controller.disConnectDevice(serial);
}

/**
 * 开始喷射香水
 *
 * @param duration         持续时间
 * @param boosterIntensity 喷射强度
 * @param fanIntensity     风扇强度
 * @param portTemplate     格式：portNumber|intensity,portNumber|intensity
 */
export function play(
  duration: number,
  boosterIntensity: number,
  fanIntensity: number,
  portTemplate: string
): Promise<any> {
  return Controller.play(
    duration,
    boosterIntensity,
    fanIntensity,
    portTemplate
  );
}

export function stop(serial: string): Promise<any> {
  return Controller.stop(serial);
}
