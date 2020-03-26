var exec = require('cordova/exec');

module.exports = {
    callNative: function (name, args, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'MqttPlugin', name, args);
    },
    connect: function (config, errorCallback) {
        this.callNative('connect', [config], null, errorCallback);
    },
    subscribe: function (topicName, qos, errorCallback) {
        this.callNative('subscribe', [{
            topic: topicName,
            qos: qos
        }], null, errorCallback);
    },
    unsubscribe: function (topicName, errorCallback) {
        this.callNative('unsubscribe', [topicName], null, errorCallback);
    },
    disconnect(errorCallback) {
        this.callNative('disconnect', null, null, errorCallback);
    },
    publish(config, errorCallback) {
        this.callNative('publish', [{
            ...{
                topic: '',
                payload: '',
                qos: 0,
                retain: false,
            },
            ...config
        }], null, errorCallback);
    },
    listen(successCallback, errorCallback) {
        this.callNative('listen', [], successCallback, errorCallback);
    },
    isConnected(successCallback, errorCallback) {
        this.callNative('isConnected', [], successCallback, errorCallback);
    }

}