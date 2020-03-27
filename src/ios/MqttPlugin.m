
#import "MqttPlugin.h"
#import <Cordova/CDVViewController.h>
#import "MqttDelegate.h"

@interface MqttPlugin (){
//    NSString *_commandId;
}

@end

@implementation MqttPlugin

-(void)listen:(CDVInvokedUrlCommand *)command{
    NSString *_commandId = command.callbackId;
    [[MqttDelegate sharedMqttDelegate] listen:^(BOOL error, NSString *event, NSDictionary *data, NSString *errorReason) {
        CDVPluginResult *result;
        if (error) {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorReason];
        } else {
            NSDictionary *dict = @{
                @"event":event,
                @"data":data
            };
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
        }
        [result setKeepCallbackAsBool:true];
        [self.commandDelegate sendPluginResult:result callbackId:_commandId];
    }];
}

-(void)connect:(CDVInvokedUrlCommand *)command{
    NSDictionary *config = [command.arguments objectAtIndex:0];
    [[MqttDelegate sharedMqttDelegate] connect:config];
}

-(void)subscribe:(CDVInvokedUrlCommand *)command{
    NSDictionary *config = [command.arguments objectAtIndex:0];
    NSString *topicName = [config objectForKey:@"topic"];
    int qos= [[config objectForKey:@"qos"] intValue];
    [[MqttDelegate sharedMqttDelegate] subscribe:topicName Qos:qos];
}

-(void)isConnected:(CDVInvokedUrlCommand *)command{
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:[[MqttDelegate sharedMqttDelegate] isConnected]];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

-(void)disconnect:(CDVInvokedUrlCommand *)command{
    [[MqttDelegate sharedMqttDelegate] disconnect];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

-(void)unsubscribe:(CDVInvokedUrlCommand *)command{
    NSString *topicName = [command.arguments objectAtIndex:0];
    if (topicName == nil || topicName.length == 0) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Topic name == nil."] callbackId:command.callbackId];
        return;
    }
    [[MqttDelegate sharedMqttDelegate] unsubscribe:topicName];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

-(void)publish:(CDVInvokedUrlCommand *)command{
    NSDictionary *config = [command.arguments objectAtIndex:0];
    NSString *topicName = [config objectForKey:@"topic"];
    NSString *payload = [config objectForKey:@"payload"];
    int qos = [[config objectForKey:@"qos"] intValue];
    BOOL retain = [[config objectForKey:@"retain"] boolValue];
    
    [[MqttDelegate sharedMqttDelegate]publish:topicName Payload:payload Qos:qos Retain:retain];
}

@end
