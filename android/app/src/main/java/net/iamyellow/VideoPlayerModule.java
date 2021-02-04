package net.iamyellow;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.HashMap;
import java.util.Map;

public class VideoPlayerModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;
  private static final String EVENT_NAME = "iay::VLCPlayer::js::onChange";

  VideoPlayerModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  @Override
  public String getName() {
    return "IAYVideoPlayerModule";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("EVENT_NAME", EVENT_NAME);
    return constants;
  }
}
