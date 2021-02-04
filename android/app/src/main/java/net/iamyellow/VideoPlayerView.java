package net.iamyellow;

// https://code.videolan.org/videolan/libvlc-android-samples/-/blob/master/java_sample/src/main/java/org/videolan/javasample/JavaActivity.java
// https://github.com/mafanwei/libvlc/blob/cfdff36226870de3b6fd97df0e180b5fce7b2301/vlclibrary/src/main/java/com/mafanwei/vlclibrary/VlcVideoLibrary.java

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;

import com.vlctests.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

public class VideoPlayerView extends FrameLayout implements TextureView.SurfaceTextureListener {

  private TextureView mTextureView = null;
  private LibVLC mLibVLC = null;
  private MediaPlayer mPlayer = null;

  public VideoPlayerView(@NonNull final Context context) {
    super(context);

    LayoutInflater inflater = (LayoutInflater)
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.iay_video_player_view, this, true);
    mTextureView = (TextureView) getChildAt(0);
    mTextureView.setSurfaceTextureListener(this);

    mLibVLC = new LibVLC(context);
    mPlayer = new MediaPlayer(mLibVLC);

    mPlayer.setScale(0);
    mPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_BEST_FIT);
    //mPlayer.setAspectRatio("16:9");
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    IVLCVout ivlcVout = mPlayer.getVLCVout();
    ivlcVout.setVideoSurface(surface);
    ivlcVout.setWindowSize(width, height);
    ivlcVout.attachViews();

    Media media = new Media(mLibVLC, Uri.parse("rtsp://admin:ESVLSW@alfredvpn.mooo.com:5543"));
    media.setHWDecoderEnabled(true, false);
    mPlayer.setMedia(media);
    media.release();
    mPlayer.play();
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    IVLCVout ivlcVout = mPlayer.getVLCVout();
    ivlcVout.setWindowSize(width, height);
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {
  }
}
