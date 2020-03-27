//
//  MqttDelegate.m
//  MQTT-Sample
//
//  Created by ins2 on 2020/3/26.
//

#import <Foundation/Foundation.h>
#import "MqttDelegate.h"

@interface MqttDelegate(){
    MQTTSessionManager* _mqttSession;
    eventHandle _eventHandle;
}

@end

@implementation MqttDelegate

+(MqttDelegate *)sharedMqttDelegate{
    static MqttDelegate *mqttDelegate = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        mqttDelegate = [[self alloc] init];
    });
    return mqttDelegate;
}

-(void)didFinishLaunchingWithOptions:(NSDictionary *)launchOptions andApplication:(UIApplication *)application{

}

-(void)listen:(eventHandle)handle{
    _eventHandle = handle;
}

-(void)connect:(NSDictionary *)config{
    NSString *url = [config objectForKey:@"url"];
    //    NSString *url = @"10.191.0.143";
    int port = [[config objectForKey:@"port"] intValue];
    NSString *clientId = [config objectForKey:@"clientId"];
    int connectionTimeout = [[config objectForKey:@"connectionTimeout"] intValue];
    int keepAliveInterval = [[config objectForKey:@"keepAliveInterval"] intValue];
    NSString *username = [config objectForKey:@"username"];
    NSString *password = [config objectForKey:@"password"];
    int keepAlive = [[config objectForKey:@"keepAlive"] intValue];
    BOOL isBinaryPayload = [[config objectForKey:@"isBinaryPayload"] boolValue];
    BOOL automaticReconnect = [[config objectForKey:@"automaticReconnect"] boolValue];

    BOOL auth = (username != nil && password != nil&& username.length != 0 && password.length != 0);

    if (_mqttSession == nil) {
        _mqttSession = [[MQTTSessionManager alloc] init];
        [_mqttSession addObserver:self
                       forKeyPath:@"state"
                          options:NSKeyValueObservingOptionInitial | NSKeyValueObservingOptionNew
                          context:nil];
    }

//    MQTTCFSocketTransport *transport = [[MQTTCFSocketTransport alloc] init];
//    transport.host = url;
//    transport.port = port;
//    session.transport = transport;
//    _mqttSession.delegate = self;
//    session.delegate = self;
//    MQTTSession *session = _mqttSession.session;
//    session.delegate = self;
    [_mqttSession connectTo:url port:port tls:false keepalive:keepAlive clean:true auth:auth user:username pass:password will:FALSE willTopic:nil willMsg:nil willQos:MQTTQosLevelAtMostOnce willRetainFlag:false withClientId:clientId securityPolicy:nil certificates:nil protocolLevel:MQTTProtocolVersion31 connectHandler:^(NSError *error) {
        NSLog(@"Connect error: %@",error);
    }];
    _mqttSession.delegate = self;
}

-(void)subscribe:(NSString *)topicName Qos:(int)qos{
    if ([self isConnected]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                //                [self->_mqttSession.session subscribeToTopic:topicName atLevel:qos];
                [self->_mqttSession.session subscribeToTopic:topicName atLevel:MQTTQosLevelAtMostOnce subscribeHandler:^(NSError *error, NSArray<NSNumber *> *gQoss) {
                    if (error != nil) {
                        [self postFailureEvent:@"subscribe" Data:error.localizedDescription];
                    }
                }];
            });
        });
    }
}

-(Boolean)isConnected{
    return _mqttSession.state == MQTTSessionManagerStateConnected;
}

-(void)disconnect{
    if ([self isConnected]) {
        [_mqttSession disconnectWithDisconnectHandler:^(NSError *error) {
            if (error != nil) {
                [self postFailureEvent:@"disConnect" Data:error.localizedDescription];
            }
        }];
//        [_mqttSession.session disconnect];
    }
}

-(void)unsubscribe:(NSString *)topicName{
    if ([self isConnected]) {
        [_mqttSession.session unsubscribeTopic:topicName];
    }
}

-(void)publish:(NSString *)topic Payload:(NSString *)payload Qos:(int)qos Retain:(Boolean)retain{
    if ([self isConnected]) {
        [_mqttSession.session publishData:[payload dataUsingEncoding:NSUTF8StringEncoding] onTopic:topic retain:retain qos:qos];
    }
}

-(void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context{
    switch (_mqttSession.state) {
        case MQTTSessionManagerStateClosed:
            [self postSuccessEvent:@"connectionLost" Data:@{}];
            break;
        case MQTTSessionManagerStateClosing:
            break;
        case MQTTSessionManagerStateConnected:
            [self postSuccessEvent:@"connected" Data:@{}];
            break;
        case MQTTSessionManagerStateConnecting:
            break;
        case MQTTSessionManagerStateError:
            [self postFailureEvent:@"MQTTSessionManagerStateError" Data:@""];
            break;
        case MQTTSessionManagerStateStarting:
        default:
            break;
    }
}


-(void)handleMessage:(NSData *)data onTopic:(NSString *)topic retained:(BOOL)retained{
    NSDictionary *result = @{
        @"topic":topic,
        @"payload":[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]
    };
    [self postSuccessEvent:@"message" Data:result];
}

-(void) postSuccessEvent: (NSString *)event Data: (NSDictionary *)data{
    if (_eventHandle == nil) {
        return;
    }
    _eventHandle(false, event, data, nil);
}

-(void) postFailureEvent: (NSString *)event Data: (NSString *)reason{
    if (_eventHandle == nil) {
        return;
    }
    _eventHandle(true, event, nil, reason);
}


@end
