import { StyleProp, ViewStyle } from 'react-native'

declare type VideoPlayerViewProps = {
  style?: StyleProp<ViewStyle>
  sourceUri: string
  paused: boolean
  muted?: boolean
  volume?: number
  videoAspectRatio?: string
  playInBackground?: boolean
  onPlaying?: () => void
  onPaused?: () => void
  onEnded?: () => void
  onStopped?: () => void
  onError?: () => void
}

declare const VideoPlayerView: (props: VideoPlayerViewProps) => JSX.Element
