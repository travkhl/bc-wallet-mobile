import {
  Button,
  ButtonType,
} from '@bifold/core'
import React, { useEffect, useState, useRef } from 'react'
import { StyleSheet, View } from 'react-native'
import { SafeAreaView } from 'react-native-safe-area-context'

import { Camera, useCameraDevice, useCameraPermission, useFrameProcessor, VisionCameraProxy, Frame, runAtTargetFps, useCodeScanner } from 'react-native-vision-camera'

// mlkit implementation
const plugin = VisionCameraProxy.initFrameProcessorPlugin('dualBarcodeScan')
export function dualBarcodeScan(frame: Frame) {
  'worklet'
  if (plugin == null) {
    throw new Error("Failed to load Frame Processor Plugin!")
  }
  return plugin.call(frame)
}

// zebrax implementation
// const plugin = VisionCameraProxy.initFrameProcessorPlugin('zebrascan')
// export function zebrascan(frame: Frame) {
//   'worklet'
//   if (plugin == null) {
//     throw new Error("Failed to load Frame Processor Plugin!")
//   }
//   return plugin.call(frame)
// }

const Terms: React.FC = () => {
  const style = StyleSheet.create({
    safeAreaView: {
      flex: 1,
    },
  })

  // react-native-vision-camera codes scanner implementation
  const codeScannerExperimental = useCodeScanner({
    codeTypes: ['pdf-417', 'code-128'],
    onCodeScanned: (val) => {
      console.log(`Scanned codes: ${val.length}`)
      val.forEach(code => {
        console.log(`Code Type: ${code.type}, Value: ${code.value ?? 'N/A'}`)
      })
    },
  })

  const { hasPermission, requestPermission } = useCameraPermission()
  const device = useCameraDevice('back')

  useEffect(() => {
    if (!hasPermission) {
      requestPermission()
    }
  }, [hasPermission, requestPermission])

  const camera = useRef<Camera>(null)

  // does nothing
  const handleCapturePhoto = async () => {
    try {
      if (camera.current) {
        const photo = await camera.current.takePhoto()
        console.log('Photo captured:', photo)
        // capture image data to variable
        const result = fetch(`file://${photo.path}`)
        const data = (await result).blob()
        console.log('Photo data:', data)
      }
    } catch (error) {
      console.error(error)
    }
  }

  const frameProcessor = useFrameProcessor((frame) => {
    'worklet';
    runAtTargetFps(1, () => {
      const result = dualBarcodeScan(frame)
      // const result = zebrascan(frame)
      console.log(result)
    })
  }, [])

  return (
    <SafeAreaView edges={['left', 'right', 'bottom']} style={style.safeAreaView}>
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        {device && hasPermission && (
          <>
            <Camera
              style={StyleSheet.absoluteFill}
              ref = {camera}
              device={device}
              isActive={true}
              photo={true}
              frameProcessor={frameProcessor}
              // frameProcessorFps={1} 
              enableFpsGraph={true}
              // codeScanner={codeScannerExperimental}
            />
          </>
        )}
      </View>
      <View style={{ justifyContent: 'center', alignItems: 'center' }}> 
        <Button
          title="Capture"
          onPress={handleCapturePhoto}
          buttonType={ButtonType.Primary}
        />
      </View>
    </SafeAreaView>
  )
}

export default Terms
