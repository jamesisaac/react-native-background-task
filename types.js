// @flow

/**
 * See README.md for documentation
 */
export type ScheduleOptions = {
  period?: number,
  timeout?: number,
  flex?: number,
}

/**
 * See README.md for documentation
 */
export type BackgroundTaskInterface = {
  define: (task: () => void) => void,
  schedule: (options?: ScheduleOptions) => void,
  cancel: () => void,
  finish: () => void,
}