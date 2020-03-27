# cordova-plugin-log2c-mqtt

Cordova MQTT Plugin,`iOS`基于[MQTT-Client-Framework](https://github.com/novastone-media/MQTT-Client-Framework),`Android`基于[paho.mqtt.android](https://github.com/eclipse/paho.mqtt.android)

## Install

```
cordova plugin add cordova-plugin-log2c-mqtt
```

## Usage

```javascript

/**
 * 监听事件
 * const successCallback = (data)=>{
 *    //data.event 事件:
 *    // 'connected' 成功连接
 *    // 'connectionLost' 断开连接
 *    // 'message' 收到消息,`data.data`是消息body
 *    // etc...
 *  }
 */
listen(successCallback, errorCallback){};

/**
  * 连接
  *const config = {
  *     url: '',
  *     port: 1883,
  *     clientId: '',
  *     connectionTimeout: 3000,
  *     keepAliveInterval: 10,
  *     automaticReconnect: true,
  *     username: '',
  *     password: '',
  *     keepAlive: 60,
  *     isBinaryPayload: false,
  *     enableSSL: false
  * }
  * // 'username'||'password'其中一个为空,则默认无账号密码连接
  * // 'enableSSL' 开发中,暂时无效
  */
connect(config, errorCallback){};

/**
 * 订阅 topic
 */
subscribe(topicName, qos, errorCallback){};

/**
 * 取消订阅 topic
 */
unsubscribe(topicName, errorCallback){};

/**
 * 断开连接
 */
disconnect(errorCallback){};

/**
  * 发布消息
  *const config = {
  *     topic: '',
  *     qos: 0,
  *     payload: '', // 消息内容
  *     retain: true
  * }
  */
publish(config, errorCallback){};

/**
 * 判断是否已连接
 * successCallback 返回 Boolean
 */
isConnected(successCallback, errorCallback){};
```

## TODO
1. SSL 连接
