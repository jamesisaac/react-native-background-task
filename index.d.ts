type ScheduleOptions = {
    period?: number,
    timeout?: number,
    flex?: number
}
  
type StatusResponse = {
    available: boolean,
    unavailableReason?: 'denied' | 'restricted'
}

type BackgroundTaskInterface = {    
    define: (task: () => void) => void,
    schedule: (options?: ScheduleOptions) => void,
    finish: () => void,
    cancel: () => void,
    statusAsync: () => Promise<StatusResponse>
    }

declare const backgroundTask: BackgroundTaskInterface
export default backgroundTask;