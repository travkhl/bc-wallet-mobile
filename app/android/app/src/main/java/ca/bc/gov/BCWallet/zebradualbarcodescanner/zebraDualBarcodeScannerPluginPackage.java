package ca.bc.gov.BCWallet.zebradualbarcodescanner;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.mrousavy.camera.frameprocessors.FrameProcessorPluginRegistry;

import java.util.Collections;
import java.util.List;

public class zebraDualBarcodeScannerPluginPackage implements ReactPackage {
  static {
    FrameProcessorPluginRegistry.addFrameProcessorPlugin(
            "zebrascan",
            (proxy, options) -> new zebraDualBarcodeScannerPlugin(proxy, options)
    );
  }

  @NonNull
  @Override
  public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }

  @NonNull
  @Override
  public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}