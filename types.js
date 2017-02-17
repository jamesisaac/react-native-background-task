// @flow

export type RegisterOptions = {
  period?: number,
  timeout?: number,
}

export type BackgroundTaskInterface = {
  register: (task: () => void, options?: RegisterOptions) => void,
  cancel: () => void,
  finish: () => void,
}