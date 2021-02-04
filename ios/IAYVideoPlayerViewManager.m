//
//  IAYVLCViewManager.m
//  VLCTests
//
//  Created by yellow on 2/2/21.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "IAYVideoPlayerViewManager.h"

#import "IAYVideoPlayerView.h"

@implementation IAYVideoPlayerViewManager

RCT_EXPORT_MODULE(IAYVideoPlayerView)

RCT_EXPORT_VIEW_PROPERTY(listenerId, NSInteger)

RCT_EXPORT_VIEW_PROPERTY(sourceUri, NSString*)
RCT_EXPORT_VIEW_PROPERTY(paused, BOOL)
RCT_EXPORT_VIEW_PROPERTY(muted, BOOL)
RCT_EXPORT_VIEW_PROPERTY(volume, NSInteger)
RCT_EXPORT_VIEW_PROPERTY(videoAspectRatio, NSString*)
RCT_EXPORT_VIEW_PROPERTY(playInBackground, BOOL)

-(UIView*)view
{
  IAYVideoPlayerView* view = IAYVideoPlayerView.new;
  return view;
}

@end
