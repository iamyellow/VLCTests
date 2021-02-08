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

@property (nonatomic, assign) BOOL needsFireViewingJsEvent;

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

#pragma mark - js land

-(void)setListenerId:(NSInteger)listenerId
{
  _listenerId = listenerId;
}

-(void)setSourceUri:(NSString *)sourceUri
{
  [self stop];
  
  _sourceUri = sourceUri;
  _mediaPlayer.media = [VLCMedia mediaWithURL:[NSURL URLWithString:_sourceUri]];
  
  if (!_paused) {
    [self play];
  }
}

-(void)setPaused:(BOOL)paused
{
  _paused = paused;
  
  if (!paused) {
    [self play];
  }
  else {
    [self pause];
  }
}

-(void)setMuted:(BOOL)muted
{
  _muted = muted;
  _mediaPlayer.audio.muted = _muted;
}

-(void)setVolume:(NSInteger)volume
{
  _volume = volume;
  _mediaPlayer.audio.volume = @(_volume).intValue;
}

-(void)setVideoAspectRatio:(NSString *)videoAspectRatio
{
  _videoAspectRatio = videoAspectRatio;
  _mediaPlayer.videoAspectRatio = strdup(_videoAspectRatio.UTF8String);
}

-(void)setPlayInBackground:(BOOL)playInBackground
{
  _playInBackground = playInBackground;
}

#pragma mark - play state helpers

-(void)play
{
  if (_mediaPlayer.media && !_mediaPlayer.isPlaying) {
    [_mediaPlayer play];
  }
}

-(void)pause
{
  if (_mediaPlayer.media && _mediaPlayer.isPlaying) {
    [_mediaPlayer pause];
  }
}

-(void)stop
{
  if (_mediaPlayer.media && _mediaPlayer.isPlaying) {
    [_mediaPlayer stop];
  }
}

#pragma mark - view lifecycle

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
    
    if (!_paused) {
      [self play];
    }
  } else {
    [self stop];
    
    _mediaPlayer.drawable = nil; // IMPORTANT: player will retain (strong) the view otherwise
    _mediaPlayer.delegate = nil;

    [[NSNotificationCenter defaultCenter] removeObserver:self];
  }

  [super didMoveToWindow];
}

#pragma mark - app lifecycle

-(void)applicationWillEnterForeground:(NSNotification*)notif
{
  if (!_paused) {
    [self play];
  }
}

-(void)applicationWillResignActive:(NSNotification*)notif
{
  if (!_playInBackground) {
    [self pause];
  }
}

#pragma mark - VLCMediaPlayerDelegate

-(void)mediaPlayerStateChanged:(NSNotification*)notification
{
  // NSLog(@"*** _mediaPlayer.state = ", VLCMediaPlayerStateToString(_mediaPlayer.state));
        
  NSString* kind;
  switch (_mediaPlayer.state) {
    case VLCMediaPlayerStateOpening:
      _needsFireViewingJsEvent = YES;
      break;
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
  
  if (kind) {
    NSDictionary* event = @{@"id": @(self.listenerId), @"kind": kind};
    [IAYVideoPlayerModule emitPlayerStateEvent:event];
  }
}

-(void)mediaPlayerTimeChanged:(NSNotification*)aNotification
{
  if (_mediaPlayer.time.intValue > 0 && _needsFireViewingJsEvent) {
    _needsFireViewingJsEvent = NO;
    
    NSDictionary* viewingEvent = @{@"id": @(self.listenerId), @"kind": @"viewing"};
    [IAYVideoPlayerModule emitPlayerStateEvent:viewingEvent];
  }
}

@end
