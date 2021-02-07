package net.iamyellow;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import javax.annotation.Nonnull;

public class VideoPlayerViewManager  extends SimpleViewManager<VideoPlayerView> {

  @Override
  public String getName() {
    return "IAYVideoPlayerView";
  }

  @Override
  public VideoPlayerView createViewInstance(ThemedReactContext context) {
    return new VideoPlayerView(context);
  }

  @Override
  public void onDropViewInstance(@Nonnull VideoPlayerView view) {
    super.onDropViewInstance(view);
    view.onReactUnmount();
  }

  @ReactProp(name = "listenerId")
  public void setListenerId(VideoPlayerView view, int listenerId) {
    view.setListenerId(listenerId);
  }

  @ReactProp(name = "sourceUri")
  public void setSourceUri(VideoPlayerView view, String sourceUri) {
    view.setSourceUri(sourceUri);
  }

  @ReactProp(name = "paused")
  public void setPaused(VideoPlayerView view, boolean paused) {
    view.setPaused(paused);
  }

  @ReactProp(name = "muted")
  public void setMuted(VideoPlayerView view, boolean muted) {
    view.setMuted(muted);
  }

  @ReactProp(name = "volume")
  public void setVolume(VideoPlayerView view, int volume) {
    view.setVolume(volume);
  }

  @ReactProp(name = "videoAspectRatio")
  public void setVideoAspectRatio(VideoPlayerView view, String videoAspectRatio) {
    view.setVideoAspectRatio(videoAspectRatio);
  }

  @ReactProp(name = "playInBackground")
  public void setPlayInBackground(VideoPlayerView view, boolean playInBackground) {
    view.setPlayInBackground(playInBackground);
  }
}