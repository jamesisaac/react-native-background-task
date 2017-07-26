// @flow

import BackgroundFetch from 'react-native-background-fetch'
import constants from './contants'
import type { BackgroundTaskInterface, StatusResponse } from '../types'

const BackgroundTask: BackgroundTaskInterface = {
  ...constants,

  _definition: null,

  define: function(task) {
    this._definition = task
  },

  schedule: function(options = {}) {
    // Cancel existing tasks
    BackgroundFetch.stop()

    // Configure the native module
    // Automatically calls RNBackgroundFetch#start
    BackgroundFetch.configure(
      { stopOnTerminate: false },
      this._definition,
      () => {
        console.warn(`Background Fetch failed to start`)
      }
    )
  },

  finish: function() {
    BackgroundFetch.finish()
  },

  cancel: function() {
    BackgroundFetch.stop()
  },

  statusAsync: function() {
    return new Promise(resolve => {
      BackgroundFetch.status(status => {
        if (status === BackgroundFetch.STATUS_RESTRICTED) {
          return resolve({
            available: false,
            unavailableReason: 'restricted',
          })
        } else if (status === BackgroundFetch.STATUS_DENIED) {
          return resolve({
            available: false,
            unavailableReason: 'denied',
          })
        }

        return resolve({
          available: true,
        })
      })
    })
  }
}

module.exports = BackgroundTask
