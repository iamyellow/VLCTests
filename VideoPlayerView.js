import React, { useEffect, useRef, useState } from 'react'
import {
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

  useEffect(() => {
    propsRef.current = props
  }, [props])

  useEffect(() => {
    lastListenerId += 1

    const eventEmitter = Platform.select({
      ios: new NativeEventEmitter(VideoPlayerModule),
      android: {
        addListener: () => {
          return {
            remove: () => {}
          }
        }
      }
    })

    const subscription = eventEmitter.addListener(
      VideoPlayerModule.EVENT_NAME,
      event => {
        const { id, kind } = event
        if (id !== myListenerId) {
          return
        }

        if (kind === 'playing') {
          if (propsRef.current.onPlaying) {
            propsRef.current.onPlaying()
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
      subscription.remove()
    }
  }, [])

  return <NativeVideoPlayerView listenerId={myListenerId} {...props} />
}
