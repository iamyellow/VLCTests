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
    // 'http://streams.videolan.org/streams/mp4/Mr_MrsSmith-h264_aac.mp4'
    //'rtsp://admin:ESVLSW@alfredvpn.mooo.com:5543'
    'rtsp://admin:ESVLSWa@alfredvpn.mooo.com:5543'
  )

  /*useEffect(() => {
    const timer = setTimeout(() => {
      console.log('*** PERKINS go')
      setSourceUri(
        'http://streams.videolan.org/streams/mp4/Mr_MrsSmith-h264_aac.mp4'
      )
    }, 25 * 1000)

    return () => {
      clearTimeout(timer)
    }
  }, [])*/

  return (
    <VideoPlayerView
      style={styles.video}
      sourceUri={sourceUri}
      paused={false}
      playInBackground={false}
      onPlaying={() => {
        console.log('*** playing')
      }}
      onViewing={() => {
        console.log('*** viewing')
      }}
      onPaused={() => {
        // console.log('*** pause')
      }}
      onError={() => {
        console.log('*** error')
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
