//
//  IAYVLCView.m
//  VLCTests
//
//  Created by yellow on 2/2/21.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "IAYVideoPlayerView.h"

#import <MobileVLCKit/VLCMediaPlayer.h>
#import <React/UIView+React.h>

#import "IAYVideoPlayerModule.h"

@interface IAYVideoPlayerView () <VLCMediaPlayerDelegate>

@property (nonatomic, strong) VLCMediaPlayer* mediaPlayer;

@property (nonatomic, assign) NSInteger listenerId;

@property (nonatomic, strong) NSString* sourceUri;
@property (nonatomic, assign) BOOL paused;
@property (nonatomic, assign) BOOL muted;
@property (nonatomic, assign) NSInteger volume;
@property (nonatomic, strong) NSString* videoAspectRatio;
@property (nonatomic, assign) BOOL playInBackground;
@property (nonatomic, assign) BOOL wasPlayingBeforeResignActive;

@end

@implementation IAYVideoPlayerView

-(id)init
{
  self = [super init];
  if (self) {
    _mediaPlayer = [[VLCMediaPlayer alloc] init];
  }
  return self;
}

-(void)didMoveToWindow
{
  BOOL isVisible = self.superview && self.window;
  if (isVisible) {
    _mediaPlayer.delegate = self;
    _mediaPlayer.drawable = self;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                                selector:@selector(applicationWillResignActive:)
                                                    name:UIApplicationWillResignActiveNotification
                                                  object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationWillEnterForeground:)
                                                 name:UIApplicationWillEnterForegroundNotification
                                               object:nil];
  } else {
    if (_mediaPlayer.isPlaying) {
      [_mediaPlayer stop];
    }
    _mediaPlayer.drawable = nil; // IMPORTANT: player will retain (strong) the view otherwise
    _mediaPlayer.delegate = nil;
    _wasPlayingBeforeResignActive = NO;
  }

  [super didMoveToWindow];
}

-(void)didSetProps:(NSArray<NSString*>*)changedProps
{
  BOOL sourceUriDidChange = [changedProps indexOfObject:@"sourceUri"] != NSNotFound;
  if (sourceUriDidChange) {
    if (_mediaPlayer.isPlaying) {
      [_mediaPlayer stop];
    }
    _mediaPlayer.media = [VLCMedia mediaWithURL:[NSURL URLWithString:_sourceUri]];
  }
  
  if ([changedProps indexOfObject:@"paused"] != NSNotFound || sourceUriDidChange) {
    if (_paused) {
      [_mediaPlayer pause];
    }
    else {
      [_mediaPlayer play];
    }
  }
  
  if ([changedProps indexOfObject:@"muted"] != NSNotFound) {
    _mediaPlayer.audio.muted = _muted;
  }
  
  if ([changedProps indexOfObject:@"volume"] != NSNotFound) {
    _mediaPlayer.audio.volume = @(_volume).intValue;
  }
  
  if ([changedProps indexOfObject:@"videoAspectRatio"] != NSNotFound) {
    _mediaPlayer.videoAspectRatio = strdup(_videoAspectRatio.UTF8String);
  }
}

#pragma mark - App lifecycle

-(void)applicationWillResignActive:(NSNotification*)notif
{
  if (!_playInBackground) {
    _wasPlayingBeforeResignActive = _mediaPlayer.isPlaying && !_paused;
    if (_mediaPlayer.isPlaying) {
      [_mediaPlayer stop];
    }
  }
}

-(void)applicationWillEnterForeground:(NSNotification*)notif
{
  if (!_playInBackground) {
    if (_wasPlayingBeforeResignActive) {
      [_mediaPlayer play];
    }
  }
}

#pragma mark - VLCMediaPlayerDelegate

-(void)mediaPlayerStateChanged:(NSNotification*)notification
{
  NSString* kind;
  switch (_mediaPlayer.state) {
    case VLCMediaPlayerStatePlaying:
      kind = @"playing";
      break;
    case VLCMediaPlayerStatePaused:
      kind = @"paused";
      break;
    case VLCMediaPlayerStateStopped:
      kind = @"stopped";
      break;
    case VLCMediaPlayerStateEnded:
      kind = @"ended";
      break;
    case VLCMediaPlayerStateError:
      kind = @"error";
      break;
    default:
      break;
  }
  
  if (!kind) {
    return;
  }
  
  NSDictionary* event = @{@"id": @(self.listenerId), @"kind": kind};
  [IAYVideoPlayerModule emitPlayerStateEvent:event];
}

@end
