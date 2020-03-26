
#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>


@interface MqttPlugin : CDVPlugin

- (void)checkUpdate:(CDVInvokedUrlCommand*)command;

- (void)setConfig:(CDVInvokedUrlCommand*)command;

@end
