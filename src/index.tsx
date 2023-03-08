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

export function checkBluetooth(): Promise<number> {
  return Controller.checkBluetooth();
}

export function checkPermission(): Promise<number> {
  return Controller.checkPermission();
}

export function deviceList(): Promise<number> {
  return Controller.deviceList();
}

export function deviceCacheList(): Promise<number> {
  return Controller.deviceCacheList();
}

export function connectDevice(serial: string): Promise<number> {
  return Controller.connectDevice(serial);
}

export function disConnectDevice(serial: string): Promise<number> {
  return Controller.connectDevice(serial);
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
export function play(
  duration: number,
  boosterIntensity: number,
  fanIntensity: number,
  portTemplate: string
): Promise<number> {
  return Controller.play(
    duration,
    boosterIntensity,
    fanIntensity,
    portTemplate
  );
}

export function stop(serial: string): Promise<number> {
  return Controller.stop(serial);
}
