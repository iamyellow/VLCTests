//
//  IAYVLCManager.m
//  VLCTests
//
//  Created by yellow on 2/2/21.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "IAYVideoPlayerModule.h"

static NSString* const kIAYVLCPlayerOnChangeEventName = @"iay::VideoPlayer::native::onChange";
static NSString* const kIAYVLCPlayerOnChangeJsEventName = @"iay::VideoPlayer::js::onChange";

@interface IAYVideoPlayerModule ()

@property (nonatomic, assign) BOOL hasListeners;

@end

@implementation IAYVideoPlayerModule

RCT_EXPORT_MODULE();

+(BOOL)requiresMainQueueSetup
{
  return YES;
}

-(NSDictionary*)constantsToExport
{
  return @{@"EVENT_NAME": kIAYVLCPlayerOnChangeJsEventName};
}

-(NSArray<NSString*>*)supportedEvents
{
  return @[kIAYVLCPlayerOnChangeJsEventName];
}

-(void)startObserving
{
  _hasListeners = YES;

  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(emitJsEvent:)
                                               name:kIAYVLCPlayerOnChangeEventName
                                             object:nil];
}

-(void)stopObserving
{
  _hasListeners = NO;
  
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

+(void)emitPlayerStateEvent:(NSDictionary*)event
{
  [[NSNotificationCenter defaultCenter] postNotificationName:kIAYVLCPlayerOnChangeEventName
                                                      object:self
                                                    userInfo:event];
}

-(void)emitJsEvent:(NSNotification*)notification
{
  if (!_hasListeners) {
    return;
  }
  
  [self sendEventWithName:kIAYVLCPlayerOnChangeJsEventName body:notification.userInfo];
}

@end
