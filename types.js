// @flow

/**
 * See README.md for documentation
 */
export type ScheduleOptions = {
  period?: number,
  timeout?: number,
  flex?: number,
}

export type StatusResponse = {
  available: boolean,
  unavailableReason?: 'denied' | 'restricted',
}

/**
 * See README.md for documentation
 */
export type BackgroundTaskInterface = {
  define: (task: () => void) => void,
  schedule: (options?: ScheduleOptions) => void,
  finish: () => void,
  cancel: () => void,
  statusAsync: () => Promise<StatusResponse>,
}