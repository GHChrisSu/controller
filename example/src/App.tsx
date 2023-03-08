import * as React from 'react';
import { useState } from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import {
  checkBluetooth,
  checkPermission,
  deviceList,
  deviceCacheList,
  connectDevice,
  disConnectDevice,
  play,
  stop,
} from 'aromajoin_controller';

export default function App() {
  const [serial, setSerial] = useState('');
  const [result, setResult] = useState('');

  const onCheckBluetooth = async () => {
    console.info('[onCheckBluetooth start ]');
    const state = await checkBluetooth();
    setResult(state);
    console.info('[onCheckBluetooth result]', state);
  };

  const onCheckPermission = async () => {
    console.info('[onCheckPermission start ]');
    const state = await checkPermission();
    setResult(state);
    console.info('[onCheckPermission result]', state);
  };

  const onDeviceList = async () => {
    console.info('[onDeviceList start ]');
    const state = await deviceList();
    const array = JSON.parse(state.toString());
    setResult(array);
    console.info('[onDeviceList result]', array);
    setSerial(array[0]);
  };

  const onDeviceCacheList = async () => {
    console.info('[onDeviceCacheList start ]');
    const state = await deviceCacheList();
    setResult(state);
    console.info('[onDeviceCacheList result]', state, serial);
  };

  const onConnectDevice = async () => {
    console.info('[onConnectDevice start ]');
    const state = await connectDevice(serial);
    setResult(state);
    console.info('[onConnectDevice state]', state);
  };
  const onPlay = async () => {
    console.info('[onPlay start ]');
    const state = await play(1000, 50, 200, '1|30,2|40');
    setResult(state);
    console.info('[onPlay result]', state);
  };
  const onStop = async () => {
    console.info('[onPlay start ]');
    const state = await stop(serial);
    setResult(state);
    console.info('[onPlay result]', state);
  };

  const onDisConnectDevice = async () => {
    console.info('[onDisConnectDevice start ]');
    const state = await disConnectDevice(serial);
    setResult(state);
    console.info('[onDisConnectDevice result]', state);
  };

  return (
    <View style={styles.container}>
      <View style={styles.result}>
        <Text style={styles.resultText}>Result: {result}</Text>
      </View>

      <TouchableOpacity
        onPress={() => onCheckBluetooth()}
        style={styles.button}
      >
        <Text style={styles.textColor}>获取蓝牙状态(checkBluetooth)</Text>
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => onCheckPermission()}
        style={styles.button}
      >
        <Text style={styles.textColor}>检查权限状态(checkPermission)</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => onDeviceList()} style={styles.button}>
        <Text style={styles.textColor}>获取设备列表(deviceList)</Text>
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => onDeviceCacheList()}
        style={styles.button}
      >
        <Text style={styles.textColor}>获取缓存设备列表(deviceCacheList)</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => onConnectDevice()} style={styles.button}>
        <Text style={styles.textColor}>链接设备(connectDevice)</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => onPlay()} style={styles.button}>
        <Text style={styles.textColor}>播放(play)</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => onStop()} style={styles.button}>
        <Text style={styles.textColor}>停止播放(stop)</Text>
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => onDisConnectDevice()}
        style={styles.button}
      >
        <Text style={styles.textColor}>断开链接(disConnectDevice)</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    position: 'relative',
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 5,
  },
  result: {
    position: 'absolute',
    top: 10,
    borderRadius: 10,
    borderColor: 'red',
    borderWidth: 2,
    width: '80%',
    minHeight: 100,
    margin: 20,
  },
  button: {
    backgroundColor: 'red',
    height: 50,
    paddingHorizontal: 10,
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 50,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  textColor: {
    color: 'white',
  },
  resultText: {
    padding: 10,
  },
});
