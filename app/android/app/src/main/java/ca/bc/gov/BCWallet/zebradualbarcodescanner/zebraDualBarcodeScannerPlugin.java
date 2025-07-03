package ca.bc.gov.BCWallet.zebradualbarcodescanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mrousavy.camera.frameprocessors.Frame;
import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin;
import com.mrousavy.camera.frameprocessors.VisionCameraProxy;
import java.util.Map;

public class zebraDualBarcodeScannerPlugin extends FrameProcessorPlugin {
  public zebraDualBarcodeScannerPlugin(@NonNull VisionCameraProxy proxy, @Nullable Map<String, Object> options) {
    super();
  }

  @Nullable
  @Override
  public Object callback(@NonNull Frame frame, @Nullable Map<String, Object> arguments) {
    // code goes here
    return null;
  }
}