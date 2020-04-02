# cordova-plugin-log2c-mqtt


## Usage

在`Android`的`Application`中调用

```java
import  com.log2c.cordova.plugin.mqtt.MqttHelper;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MqttHelper.getInstance().init(this); // <~~ Add this code.
    }
}
```