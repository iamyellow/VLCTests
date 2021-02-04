package net.iamyellow;

// https://code.videolan.org/videolan/libvlc-android-samples/-/blob/master/java_sample/src/main/java/org/videolan/javasample/JavaActivity.java
// https://github.com/mafanwei/libvlc/blob/cfdff36226870de3b6fd97df0e180b5fce7b2301/vlclibrary/src/main/java/com/mafanwei/vlclibrary/VlcVideoLibrary.java

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;

import com.vlctests.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

public class VideoPlayerView extends FrameLayout  {

  private VLCVideoLayout mVideoLayout = null;
  private LibVLC mLibVLC = null;
  private MediaPlayer mPlayer = null;

  public VideoPlayerView(@NonNull final Context context) {
    super(context);

    LayoutInflater inflater = (LayoutInflater)
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.iay_video_player_view, this, true);

    mVideoLayout = (VLCVideoLayout) getChildAt(0);
    mLibVLC = new LibVLC(context);
    mPlayer = new MediaPlayer(mLibVLC);

    Media media = new Media(mLibVLC, Uri.parse("rtsp://admin:ESVLSW@alfredvpn.mooo.com:5543"));
    media.setHWDecoderEnabled(true, false);
    mPlayer.setMedia(media);
    media.release();
    mPlayer.play();
    mPlayer.getVLCVout().setWindowSize(300, 200);

    mPlayer.setScale(0);
    //mPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_FIT_SCREEN);
    //mPlayer.setAspectRatio("16:9");
    mPlayer.attachViews(mVideoLayout, null, false, false);
  }

}
