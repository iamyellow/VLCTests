import React, { useEffect, useRef, useState } from 'react'
import {
  DeviceEventEmitter,
  NativeEventEmitter,
  NativeModules,
  Platform,
  requireNativeComponent
} from 'react-native'

const { IAYVideoPlayerModule: VideoPlayerModule } = NativeModules

const NativeVideoPlayerView = requireNativeComponent('IAYVideoPlayerView')

let lastListenerId = 0

export const VideoPlayerView = props => {
  const [myListenerId] = useState(lastListenerId)
  const propsRef = useRef(props)
  const mountedRef = useRef(false)

  useEffect(() => {
    propsRef.current = props
  }, [props])

  useEffect(() => {
    mountedRef.current = true
    lastListenerId += 1

    const eventEmitter = Platform.select({
      ios: new NativeEventEmitter(VideoPlayerModule),
      android: DeviceEventEmitter
    })

    const subscription = eventEmitter.addListener(
      VideoPlayerModule.EVENT_NAME,
      event => {
        if (!mountedRef.current) {
          return
        }

        const { id, kind } = event
        if (id !== myListenerId) {
          return
        }

        if (kind === 'opening') {
          if (propsRef.current.onOpening) {
            propsRef.current.onOpening()
          }
        } else if (kind === 'playing') {
          if (propsRef.current.onPlaying) {
            propsRef.current.onPlaying()
          }
        } else if (kind === 'viewing') {
          if (propsRef.current.onViewing) {
            propsRef.current.onViewing()
          }
        } else if (kind === 'paused') {
          if (propsRef.current.onPaused) {
            propsRef.current.onPaused()
          }
        } else if (kind === 'ended') {
          if (propsRef.current.onEnded) {
            propsRef.current.onEnded()
          }
        } else if (kind === 'stopped') {
          if (propsRef.current.onStopped) {
            propsRef.current.onStopped()
          }
        } else if (kind === 'error') {
          if (propsRef.current.onError) {
            propsRef.current.onError()
          }
        }
      }
    )

    return () => {
      mountedRef.current = false
      subscription.remove()
    }
  }, [])

  return <NativeVideoPlayerView listenerId={myListenerId} {...props} />
}
