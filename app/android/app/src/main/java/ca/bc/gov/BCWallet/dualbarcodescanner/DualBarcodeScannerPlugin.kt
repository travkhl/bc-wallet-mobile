package ca.bc.gov.BCWallet.dualbarcodescanner

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.mrousavy.camera.frameprocessors.Frame
import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin
import com.mrousavy.camera.frameprocessors.VisionCameraProxy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class DualBarcodeScannerPlugin(proxy: VisionCameraProxy, options: Map<String, Any>?): FrameProcessorPlugin() {
  
  private val scanner: BarcodeScanner by lazy {
    val scanneroptions = BarcodeScannerOptions.Builder()
      .setBarcodeFormats(Barcode.FORMAT_CODE_128, Barcode.FORMAT_PDF417)
      .build()
    BarcodeScanning.getClient(scanneroptions)
  }

  override fun callback(frame: Frame, arguments: Map<String, Any>?): Any? {
    return try {
      val results = mutableListOf<Map<String, Any>>()
      val image = InputImage.fromMediaImage(frame.image, 0)
      
      val latch = CountDownLatch(1)
      var scanResults: List<Barcode>? = null
      var scanError: Exception? = null
      
      scanner.process(image)
        .addOnSuccessListener { barcodes ->
          scanResults = barcodes
          latch.countDown()
        }
        .addOnFailureListener { exception ->
          scanError = exception
          latch.countDown()
        }
      
      if (latch.await(400, TimeUnit.MILLISECONDS)) {
        scanError?.let { throw it }
        
        scanResults?.forEach { barcode ->
          val barcodeType = when (barcode.format) {
            Barcode.FORMAT_CODE_128 -> "code128"
            Barcode.FORMAT_PDF417 -> "pdf417"
            else -> return@forEach
          }
          
          val barcodeValue = barcode.rawValue ?: return@forEach
          
          // bounding box coords
          val bounds = barcode.boundingBox?.let { rect ->
            listOf(
              rect.left, rect.top,     
              rect.right, rect.top,    
              rect.right, rect.bottom, 
              rect.left, rect.bottom   
            )
          } ?: listOf(0, 0, 0, 0, 0, 0, 0, 0)
          
          results.add(
            mapOf(
              "type" to barcodeType,
              "value" to barcodeValue,
              "bounds" to bounds
            )
          )
        }
      } else {
        // none found in time
        return emptyList<Map<String, Any>>()
      }
      
      results
    } catch (e: Exception) {
      emptyList<Map<String, Any>>()
    }
  }
}