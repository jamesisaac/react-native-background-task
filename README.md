# react-native-background-task

[![npm version](https://img.shields.io/npm/v/react-native-background-task.svg)](https://www.npmjs.com/package/react-native-background-task)
[![license](https://img.shields.io/github/license/jamesisaac/react-native-background-task.svg)](https://opensource.org/licenses/MIT)
[![npm downloads](https://img.shields.io/npm/dm/react-native-background-task.svg)](https://www.npmjs.com/package/react-native-background-task)

**WORK IN PROGRESS / PLEASE TEST BEFORE DEPLOYING IN PRODUCTION**

Periodic background tasks for React Native apps, cross-platform (iOS and
Android), which run even when the app is closed.

This library allows the scheduling of a single periodic task, which executes
when the app is in the background or closed, no more frequently than every 15
minutes.  Network, AsyncStorage etc can be used (anything except UI), so
perfect for things like a background data sync.

Behind the scenes, this library takes a different approach with each platform:

- **Android**: A native implementation, which provides scheduling on top of
  RN's built-in [Headless JS](https://facebook.github.io/react-native/docs/headless-js-android.html)
  (Android only).
  - Min API level: 16 (Android 4.1).
- **iOS**: [react-native-background-fetch](https://github.com/transistorsoft/react-native-background-fetch),
  which uses the iOS-specific `Background Fetch` technique.

To achieve a unified API, this package exposes the lowest common denominator
(e.g. only support for a single task, even though Android can support multiple).

For more per-platform flexibility, there are other platform-specific libraries
with more granular customisation.

## Installation

```bash
$ npm install --save react-native-background-task
```
  
### Android

1. The linking of the package can be done automatically by running:

  ```bash
  $ react-native link react-native-background-task
  ```

2. One manual step is still needed - in your project file
  `android/app/src/main/java/myapp/MainApplication.java`, add the following to
  the end of the `onCreate()` method:
  
  ```java
  BackgroundTaskPackage.useContext(this);
  ```

### iOS

Follow installation instructions for version 2.0.x of
[react-native-background-fetch](https://github.com/transistorsoft/react-native-background-fetch)
  
This library will behave correctly on iOS as long as `react-native-background-fetch`
is installed alongside it, and correctly linked.

## API

### `define(task)`

Define the JS code that this module should be executing.

- Should be called at the top level of your JS, **not** inside a component.
  This is because in headless mode no components are mounted, but the code
  still needs to be accessible.
- Will overwrite any previously defined task.

Parameters:

* **`task`**: **required** `() => void` - Function to be executed in the background

### `schedule(options)`

Specify the scheduling options for the task, and register it with the
platform's scheduler.

- Should be called from inside a component (e.g. your App's
  `componentDidMount`).  This is to avoid double-scheduling when the task
  launches in headless mode.
- Will `console.warn` if the device is restricting scheduling the task.

Parameters:

- **`options`**: `?object` - Any configuration you want to be set with
  the task.  Note that most of these will only work on one platform.
  
  - **`period?`** `number` - (Android only) Desired number of seconds between each
    execution of the task.  Even on Android, the OS will only take this as a
    recommendation, and will enforce a minimum of 15 minutes (similar to iOS).
    Default is 900 (15 minutes)
  - **`timeout?`** `number` - (Android only) Number of seconds the task will have
    to execute.  iOS has a hardcoded limit of 30 seconds.  Default 30 seconds.

### `cancel()`

Cancels any currently registered task.

### `finish()`

**Must be called at the end of your task** to indicate to the OS that it's
finished.  (Only required on iOS, no-op on Android).

## Examples

### Simple

```js
import React from 'react'
import { Text } from 'react-native'
import BackgroundTask from 'react-native-background-task'

BackgroundTask.define(() => {
  console.log('Hello from a background task')
  BackgroundTask.finish()
})

class MyApp extends React.Component {
  componentDidMount() {
    BackgroundTask.schedule()
  }
  
  render() {
    return <Text>Hello world</Text>
  }
}
```

### Fetch / store data

```js
import React from 'react'
import { AsyncStorage, Button, Text } from 'react-native'
import BackgroundTask from 'react-native-background-task'

BackgroundTask.define(async () => {
  // Fetch some data over the network which we want the user to have an up-to-
  // date copy of, even if they have no network when using the app
  const response = await fetch('http://feeds.bbci.co.uk/news/rss.xml')
  const text = await response.text()
  
  // Data persisted to AsyncStorage can later be accessed by the foreground app
  await AsyncStorage.setItem('@MyApp:key', text)
  
  // Remember to call finish()
  BackgroundTask.finish()
})

class MyApp extends React.Component {
  componentDidMount() {
    BackgroundTask.schedule({
      period: 1800, // Aim to run every 30 mins - more conservative on battery
    })
  }
  
  render() {
    return (
      <View>
        <Button
          title="Read results from AsyncStorage"
          onPress={async () => {
            const result = await AsyncStorage.getItem('@MyApp:key')
            console.log(result) 
          }}
        />
      </View>
    )
  }
}
```