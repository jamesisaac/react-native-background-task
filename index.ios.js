// @flow

import { NativeEventEmitter, NativeModules } from 'react-native'
import type { BackgroundTaskInterface } from './types'

const { RNBackgroundFetch } = NativeModules
const eventEmitter = new NativeEventEmitter(RNBackgroundFetch)

const BackgroundTask: BackgroundTaskInterface = {

  register: function(task, {} = {}) {
    // Cancel existing tasks
    RNBackgroundFetch.stop()

    // Configure the native module
    // Automatically calls RNBackgroundFetch#start
    RNBackgroundFetch.configure({
      stopOnTerminate: false,
    }, () => { console.error(`Device doesn't support Background Fetch`) })

    eventEmitter.addListener('fetch', task)

    RNBackgroundFetch.status(status => {
      if (status === RNBackgroundFetch.STATUS_RESTRICTED) {
        console.error('BackgroundFetch is restricted')
      } else if (status === RNBackgroundFetch.STATUS_DENIED) {
        console.error('BackgroundFetch is restricted')
      }
    })
  },

  cancel: function() {
    RNBackgroundFetch.stop()
  },

  finish: function() {
    RNBackgroundFetch.finish()
  },
}

module.exports = BackgroundTask