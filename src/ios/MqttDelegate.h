//
//  MqttDelegate.h
//  MQTT-Sample
//
//  Created by log2c on 2020/3/26.
//

#import <MQTTClient/MQTTClient.h>
#import <MQTTClient/MQTTSessionManager.h>

@interface MqttDelegate : NSObject<MQTTSessionManagerDelegate, MQTTSessionDelegate>

typedef void (^eventHandle)(BOOL error, NSString *event, NSDictionary *data, NSString *errorReason);

+ (MqttDelegate *)sharedMqttDelegate;

- (void)didFinishLaunchingWithOptions:(NSDictionary *)launchOptions andApplication:(UIApplication *)application;

- (void) listen: (eventHandle)handle;

- (void) connect: (NSDictionary *)config;

- (void) subscribe: (NSString *)topicName Qos: (int) qos;

- (Boolean) isConnected;

- (void) disconnect;

- (void) unsubscribe: (NSString *)topicName;

- (void) publish: (NSString *)topic Payload: (NSString *)payload Qos: (int)qos Retain: (Boolean) retain;

@end
