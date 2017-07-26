package com.jamesisaac.rnbackgroundtask;

import android.util.Log;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.concurrent.TimeUnit;
import java.util.Set;

public class BackgroundTaskModule extends ReactContextBaseJavaModule
            implements LifecycleEventListener  {

    private static final String TAG = "BackgroundTask";

    /**
     * Keep track of whether the React Native app is in the foreground (i.e. whether we're able to
     * schedule jobs or not).
     */
    private boolean mForeground = false;

    /**
     * While the app is foregrounded, keep track of the latest JobRequest here, ready to
     * re-schedule it as soon as app goes into the background.
     */
    private JobRequest mJobRequest;

    public BackgroundTaskModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    /**
     * Set the name of the native module which can be imported from JS
     */
    @Override
    public String getName() {
        return "BackgroundTask";
    }

    @Override
    public void initialize() {
        Log.d(TAG, "Initializing");
        super.initialize();

        // Read in an existing scheduled job if there is one
        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequests();
        if (jobRequests.size() > 1) {
            Log.w(TAG, "Found " + jobRequests.size() + " scheduled jobs, expecting 0 or 1");
        }
        if (!jobRequests.isEmpty()) {
            mJobRequest = jobRequests.iterator().next();
        }

        // Hook into lifecycle events so we can tell when the application is foregrounded
        ReactApplicationContext context = getReactApplicationContext();
        context.addLifecycleEventListener(this);
    }

    /**
     * Main point of interaction from JS users - allows them to specify the scheduling etc for the
     * background task.
     *
     * Default values are specified in JS (more accessible for the average user).
     *
     * @param config the config options passed in from JS:
     *      - period (required): how frequently to carry out the task in seconds
     *      - timeout (required): after how many seconds should the task be auto-killed
     */
    @ReactMethod
    public void schedule(final ReadableMap config) {
        Log.d(TAG, "@ReactMethod BackgroundTask.schedule");

        // Period can't be below 15m
        int period = config.getInt("period");
        if (period < 900) { period = 900; }

        // Flex must be between 5m and 15m
        int flex = config.getInt("flex");
        if (flex < 300) { flex = 300; }
        if (flex > 900) { flex = 900; }

        // Extra info to store with the JobRequest
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("timeout", config.getInt("timeout"));

        mJobRequest = new JobRequest.Builder(RNJob.JOB_TAG)
                .setPeriodic(TimeUnit.SECONDS.toMillis(period), TimeUnit.SECONDS.toMillis(flex))
                .setPersisted(true)
                .setExtras(extras)
                .build();

        if (!mForeground) {
            commitSchedule();
        }
    }

    /**
     * Allow the JS users to cancel the previously scheduled task.
     */
    @ReactMethod
    public void cancel() {
        Log.d(TAG, "@ReactMethod BackgroundTask.cancel");

        mJobRequest = null;
        JobManager.instance().cancelAll();
    }

    @Override
    public void onHostResume() {
        setForeground(true);
        JobManager.instance().cancelAll();
    }

    @Override
    public void onHostPause() {
        setForeground(false);
        commitSchedule();
    }

    @Override
    public void onHostDestroy() {
    }

    /**
     * Keep track of whether the RN app is in the foreground or not (note that even clicking the
     * Android menu button is enough to background the app).
     */
    private void setForeground(boolean foreground) {
        Log.d(TAG, String.format("Setting foreground %s", foreground));
        mForeground = foreground;
    }

    private void commitSchedule() {
        Log.d(TAG, "Committing job schedule");

        // Cancel any previous job
        JobManager.instance().cancelAll();

        // Schedule the new job, if the user has provided some config
        if (mJobRequest != null) {
            mJobRequest.schedule();
        }
    }
}