package net.iamyellow;

// https://code.videolan.org/videolan/libvlc-android-samples/-/blob/master/java_sample/src/main/java/org/videolan/javasample/JavaActivity.java
// https://github.com/mafanwei/libvlc/blob/cfdff36226870de3b6fd97df0e180b5fce7b2301/vlclibrary/src/main/java/com/mafanwei/vlclibrary/VlcVideoLibrary.java

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.vlctests.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

public class VideoPlayerView extends FrameLayout implements
    TextureView.SurfaceTextureListener,
    LifecycleEventListener,
    MediaPlayer.EventListener {

  private LibVLC mLibVLC = null;
  private MediaPlayer mPlayer = null;

  public VideoPlayerView(@NonNull Context context) {
    super(context);

    LayoutInflater inflater = (LayoutInflater)
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.iay_video_player_view, this, true);
    TextureView textureView = (TextureView) getChildAt(0);
    textureView.setSurfaceTextureListener(this);

    mLibVLC = new LibVLC(context);

    mPlayer = new MediaPlayer(mLibVLC);
    mPlayer.setScale(0);
    mPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_BEST_FIT);
  }

  public void onReactUnmount() { }

  private ThemedReactContext getReactContext() {
    return (ThemedReactContext) getContext();
  }

  // js land

  private int listenerId = -1;
  private int volume = 100;
  private boolean playInBackground = false;
  private boolean paused = false;

  public void setListenerId(int listenerId) {
    this.listenerId = listenerId;
  }

  public void setSourceUri(String sourceUri) {
    stop();

    Media media = new Media(mLibVLC, Uri.parse(sourceUri));
    media.setHWDecoderEnabled(true, false);
    mPlayer.setMedia(media);
    media.release();

    if (!this.paused) {
      play();
    }
  }

  public void setPaused(boolean paused) {
    this.paused = paused;

    if (!paused) {
      play();
    }
    else {
      pause();
    }
  }

  public void setVolume(int volume) {
    this.volume = volume;
    mPlayer.setVolume(volume);
  }

  public void setMuted(boolean muted) {
    mPlayer.setVolume(muted ? - 1 : this.volume);
  }

  public void setVideoAspectRatio(String videoAspectRatio) {
    mPlayer.setAspectRatio(videoAspectRatio);
  }

  public void setPlayInBackground(boolean playInBackground) {
    this.playInBackground = playInBackground;
  }

  private void emitJsEvent(String kind) {
    WritableMap payload = Arguments.createMap();
    payload.putInt("id", this.listenerId);
    payload.putString("kind", kind);

    DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter =
        getReactContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    eventEmitter.emit(VideoPlayerModule.IAY_VIDEO_PLAYER_MODULE_JS_EVENT_NAME, payload);
  }

  // play state helpers

  private void play() {
    if (!mPlayer.getVLCVout().areViewsAttached()) {
      return;
    }

    if (mPlayer.hasMedia() && !mPlayer.isPlaying()) {
      mPlayer.play();
    }
  }

  private void pause() {
    if (!mPlayer.getVLCVout().areViewsAttached()) {
      return;
    }

    if (mPlayer.hasMedia() && mPlayer.isPlaying()) {
      mPlayer.pause();
    }
  }

  private void stop() {
    if (!mPlayer.getVLCVout().areViewsAttached()) {
      return;
    }

    if (mPlayer.hasMedia() && mPlayer.isPlaying()) {
      mPlayer.stop();
    }
  }

  // LifecycleEventListener

  @Override
  public void onHostResume() {
    if (!this.paused) {
      play();
    }
  }

  @Override
  public void onHostPause() {
    if (!this.playInBackground) {
      pause();
    }
  }

  @Override
  public void onHostDestroy() {}

  // TextureView.SurfaceTextureListener

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    IVLCVout ivlcVout = mPlayer.getVLCVout();
    ivlcVout.setVideoSurface(surface);
    ivlcVout.setWindowSize(width, height);
    ivlcVout.attachViews();

    getReactContext().addLifecycleEventListener(this);
    mPlayer.setEventListener(this);

    if (!this.paused) {
      play();
    }
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    IVLCVout ivlcVout = mPlayer.getVLCVout();
    ivlcVout.setWindowSize(width, height);
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    stop();

    IVLCVout ivlcVout = mPlayer.getVLCVout();
    ivlcVout.detachViews();

    mPlayer.release();
    mLibVLC.release();

    mPlayer.setEventListener(null);
    getReactContext().removeLifecycleEventListener(this);

    return true;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

  // MediaPlayerListener

  @Override
  public void onEvent(MediaPlayer.Event event) {
    String kind = null;
    switch (event.type) {
      case MediaPlayer.Event.Playing:
        kind = "playing";
        break;
      case MediaPlayer.Event.Paused:
        kind = "paused";
        break;
      case MediaPlayer.Event.Stopped:
        kind = "stopped";
        break;
      case MediaPlayer.Event.EndReached:
        kind = "ended";
        break;
      case MediaPlayer.Event.EncounteredError:
        kind = "error";
        break;
      default:
        break;
    }

    if (kind != null) {
      this.emitJsEvent(kind);
    }
  }
}
