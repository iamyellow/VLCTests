/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { useEffect, useState } from 'react'
import { StyleSheet } from 'react-native'
import { VideoPlayerView } from './VideoPlayerView'

const App = () => {
  const [sourceUri, setSourceUri] = useState(
    'http://streams.videolan.org/streams/mp4/Mr_MrsSmith-h264_aac.mp4'
  )

  useEffect(() => {
    setTimeout(() => {
      console.log('*** go')
      setSourceUri('rtsp://admin:ESVLSW@alfredvpn.mooo.com:5543')
    }, 10 * 1000)
  }, [])

  return (
    <VideoPlayerView
      style={styles.video}
      sourceUri={sourceUri}
      paused={false}
      playInBackground={false}
      onPlaying={() => {
        console.log('*** playing')
      }}
      onPaused={() => {
        console.log('*** pause')
      }}
    />
  )
}

const styles = StyleSheet.create({
  video: {
    flex: 1
  }
})

export default App
