package com.jamesisaac.rnbackgroundtask;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * A requirement of Evernote's android-job library, mapping a job ID to a class.
 */
public class RNJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case RNJob.JOB_TAG:
                return new RNJob();
            default:
                return null;
        }
    }
}