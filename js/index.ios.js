// @flow

import BackgroundFetch from 'react-native-background-fetch'
import type { BackgroundTaskInterface } from '../types'

const BackgroundTask: BackgroundTaskInterface = {

  _definition: null,

  define: function(task) {
    this._definition = task
  },

  schedule: function({} = {}) {
    // Cancel existing tasks
    BackgroundFetch.stop()

    // Configure the native module
    // Automatically calls RNBackgroundFetch#start
    BackgroundFetch.configure(
      { stopOnTerminate: false },
      this._definition,
      () => { console.warn(`Device doesn't support Background Fetch`) }
    )

    // Query the authorization status
    BackgroundFetch.status(status => {
      if (status === BackgroundFetch.STATUS_RESTRICTED) {
        console.warn('BackgroundFetch is restricted')
      } else if (status === BackgroundFetch.STATUS_DENIED) {
        console.warn('BackgroundFetch is denied')
      }
    })
  },

  cancel: function() {
    BackgroundFetch.stop()
  },

  finish: function() {
    BackgroundFetch.finish()
  },
}

module.exports = BackgroundTask