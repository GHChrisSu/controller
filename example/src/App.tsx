import * as React from 'react';
import { useState } from 'react';
import { Button, NativeModules, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
const Controller = NativeModules.Controller;

export default function App() {

  const [serial, setSerial] = useState('');

  const onList = async () => {
    console.info('[开始链接]');
    const result = await Controller.diffuse();
    console.info('[开始链接 返回数据]', result);
  };

  const onCheckBluetooth = async () => {
    console.info('[onCheckBluetooth start ]');
    const result = await Controller.checkBluetooth();
    console.info('[onCheckBluetooth result]', result);
  };


  const onDeviceList = async () => {
    console.info('[onDeviceList start ]');
    const result = await Controller.deviceList();
    const array = JSON.parse(result.toString());
    console.info('[onDeviceList result]', array);
    setSerial(array[0]);
  };


  const onDeviceCacheList = async () => {
    console.info('[onDeviceCacheList start ]');
    const result = await Controller.deviceCacheList();
    console.info('[onDeviceCacheList result]', result, serial);
  };


  const onConnectDevice = async () => {
    console.info('[onConnectDevice start ]');
    const result = await Controller.connectDevice(serial);
    console.info('[onConnectDevice result]', result);
  };
  const onPlay = async () => {
    console.info('[onPlay start ]');
    const result = await Controller.play();
    console.info('[onPlay result]', result);
  };

  return (
    <View style={ styles.container }>
      <TouchableOpacity
        onPress={ () => onCheckBluetooth() }
        style={ { backgroundColor: 'red', height: 50, paddingHorizontal: 10, justifyContent: 'center', alignItems: 'center', borderRadius: 50 } }>
        <View>
          <Text style={ { color: 'white' } }>获取蓝牙状态(checkBluetooth)</Text>
        </View>
      </TouchableOpacity>


      <TouchableOpacity
        onPress={ () => onDeviceList() }
        style={ { backgroundColor: 'red', height: 50, paddingHorizontal: 10, justifyContent: 'center', alignItems: 'center', borderRadius: 50, marginVertical: 30 } }>
        <View>
          <Text style={ { color: 'white' } }>获取设备列表(deviceList)</Text>
        </View>
      </TouchableOpacity>


      <TouchableOpacity
        onPress={ () => onDeviceCacheList() }
        style={ { backgroundColor: 'red', height: 50, paddingHorizontal: 10, justifyContent: 'center', alignItems: 'center', borderRadius: 50, marginVertical: 30 } }>
        <View>
          <Text style={ { color: 'white' } }>获取缓存设备列表(deviceCacheList)</Text>
        </View>
      </TouchableOpacity>


      <TouchableOpacity
        onPress={ () => onConnectDevice() }
        style={ { backgroundColor: 'red', height: 50, paddingHorizontal: 10, justifyContent: 'center', alignItems: 'center', borderRadius: 50, marginVertical: 30 } }>
        <View>
          <Text style={ { color: 'white' } }>链接设备(connectDevice)</Text>
        </View>
      </TouchableOpacity>


      <TouchableOpacity
        onPress={ () => onPlay() }
        style={ { backgroundColor: 'red', height: 50, paddingHorizontal: 10, justifyContent: 'center', alignItems: 'center', borderRadius: 50, marginVertical: 30 } }>
        <View>
          <Text style={ { color: 'white' } }>播放(play)</Text>
        </View>
      </TouchableOpacity>


      <Text>Result:</Text>
      <Button title='Diffuse' onPress={ () => onList() } />

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
