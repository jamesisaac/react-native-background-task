// @flow

import { AppRegistry, NativeModules } from 'react-native'
import constants from './contants'
import type { BackgroundTaskInterface } from '../types'

const { BackgroundTask: RNBackgroundTask } = NativeModules

const BackgroundTask: BackgroundTaskInterface = {
  ...constants,

  define: function(task) {
    // Register the headless task
    const fn = async () => {
      task()
    }
    AppRegistry.registerHeadlessTask('BackgroundTask', () => fn)
  },

  schedule: function(
    {
      period = 900, // 15 minutes
      timeout = 30,
      flex,
    } = {}
  ) {
    // Default flex to within 50% of the period
    if (!flex) {
      flex = Math.floor(period / 2)
    }

    RNBackgroundTask.schedule({
      period,
      timeout,
      flex,
    })
  },

  finish: function() {
    // Needed for iOS, no-op on Android
  },

  cancel: function() {
    RNBackgroundTask.cancel()
  },

  statusAsync: function() {
    // No options exist on Android to block background tasks
    return Promise.resolve({
      available: true,
    })
  }
}

module.exports = BackgroundTask
