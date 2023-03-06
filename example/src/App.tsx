import * as React from 'react';
import { useState } from 'react';
import { Button, NativeModules, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

const Controller = NativeModules.Controller;

export default function App() {

  const [serial, setSerial] = useState('');
  const [result, setResult] = useState('');

  const onList = async () => {
    console.info('[开始链接]');
    const result = await Controller.diffuse();
    setResult(result);
    console.info('[开始链接 返回数据]', result);
  };

  const onCheckBluetooth = async () => {
    console.info('[onCheckBluetooth start ]');
    const result = await Controller.checkBluetooth();
    setResult(result);
    console.info('[onCheckBluetooth result]', result);
  };

  const onCheckPermission = async () => {
    console.info('[onCheckPermission start ]');
    const result = await Controller.checkPermission();
    setResult(result);
    console.info('[onCheckPermission result]', result);
  };


  const onDeviceList = async () => {
    console.info('[onDeviceList start ]');
    const result = await Controller.deviceList();
    const array = JSON.parse(result.toString());
    setResult(array);
    console.info('[onDeviceList result]', array);
    setSerial(array[0]);
  };


  const onDeviceCacheList = async () => {
    console.info('[onDeviceCacheList start ]');
    const result = await Controller.deviceCacheList();
    setResult(result);
    console.info('[onDeviceCacheList result]', result, serial);
  };


  const onConnectDevice = async () => {
    console.info('[onConnectDevice start ]');
    const result = await Controller.connectDevice(serial);
    console.info('[onConnectDevice result]', result);
  };
  const onPlay = async () => {
    console.info('[onPlay start ]');
    const result = await Controller.play(1000, 50, 200, '1|30,2|40');
    setResult(result);
    console.info('[onPlay result]', result);
  };

  const onDisConnectDevice = async () => {
    console.info('[onDisConnectDevice start ]');
    const result = await Controller.disConnectDevice(serial);
    setResult(result);
    console.info('[onDisConnectDevice result]', result);
  };

  return (
    <View style={ styles.container }>

      <View style={ { position: 'absolute', top: 10, borderRadius: 10, borderColor: 'white', borderWidth: 2, width: '80%', minHeight: 100, margin: 20 } }>
        <Text style={ { padding: 10 } }>Result: { result }</Text>
      </View>


      <TouchableOpacity onPress={ () => onCheckBluetooth() } style={ styles.button }>
        <Text style={ { color: 'white' } }>获取蓝牙状态(checkBluetooth)</Text>
      </TouchableOpacity>


      <TouchableOpacity onPress={ () => onCheckPermission() } style={ styles.button }>
        <Text style={ { color: 'white' } }>检查权限状态(checkPermission)</Text>
      </TouchableOpacity>


      <TouchableOpacity onPress={ () => onDeviceList() } style={ styles.button }>
        <Text style={ { color: 'white' } }>获取设备列表(deviceList)</Text>
      </TouchableOpacity>


      <TouchableOpacity onPress={ () => onDeviceCacheList() } style={ styles.button }>
        <Text style={ { color: 'white' } }>获取缓存设备列表(deviceCacheList)</Text>
      </TouchableOpacity>


      <TouchableOpacity onPress={ () => onConnectDevice() } style={ styles.button }>
        <Text style={ { color: 'white' } }>链接设备(connectDevice)</Text>

      </TouchableOpacity>


      <TouchableOpacity onPress={ () => onPlay() } style={ styles.button }>
        <Text style={ { color: 'white' } }>播放(play)</Text>

      </TouchableOpacity>


      <TouchableOpacity onPress={ () => onDisConnectDevice() } style={ styles.button }>
        <Text style={ { color: 'white' } }>断开链接(disConnectDevice)</Text>
      </TouchableOpacity>

      <Button title='Diffuse' onPress={ () => onList() } />

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
});
