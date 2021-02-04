package net.iamyellow;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class VideoPlayerViewManager  extends SimpleViewManager<VideoPlayerView> {

  @Override
  public String getName() {
    return "IAYVideoPlayerView";
  }

  @Override
  public VideoPlayerView createViewInstance(ThemedReactContext context) {
    return new VideoPlayerView(context);
  }
}