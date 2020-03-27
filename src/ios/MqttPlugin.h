
#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>


@interface MqttPlugin : CDVPlugin

-(void) listen: (CDVInvokedUrlCommand*)command;

-(void) connect: (CDVInvokedUrlCommand*)command;

-(void) subscribe: (CDVInvokedUrlCommand*)command;

-(void) isConnected: (CDVInvokedUrlCommand*)command;

-(void) disconnect: (CDVInvokedUrlCommand*)command;

-(void) unsubscribe: (CDVInvokedUrlCommand*)command;

-(void) publish: (CDVInvokedUrlCommand*)command;

@end
