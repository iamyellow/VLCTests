//
//  IAYVLCManager.h
//  VLCTests
//
//  Created by yellow on 2/2/21.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

NS_ASSUME_NONNULL_BEGIN

@interface IAYVideoPlayerModule : RCTEventEmitter <RCTBridgeModule>

+(void)emitPlayerStateEvent:(NSDictionary*)event;

@end

NS_ASSUME_NONNULL_END
