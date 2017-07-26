package com.jamesisaac.rnbackgroundtask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.evernote.android.job.Job;

/**
 * The single task which this library is able to schedule.
 *
 * The sole purpose here is to kick off the HeadlessTaskService, and pass along the config params
 * which were used when creating the job.
 */
public class RNJob extends Job {
    public static final String JOB_TAG = "rnbgtask";

    private static final String TAG = "BackgroundTask";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        Log.d(TAG, "Job is running");
        PersistableBundleCompat requestExtras = params.getExtras();

        // Two different types of bundle so need to re-package
        Bundle headlessExtras = new Bundle();
        headlessExtras.putInt("timeout", requestExtras.getInt("timeout", 30));

        Context context = getContext().getApplicationContext();
        Intent service = new Intent(context, HeadlessTaskService.class);
        service.putExtras(headlessExtras);
        context.startService(service);

        return Result.SUCCESS;
    }
}