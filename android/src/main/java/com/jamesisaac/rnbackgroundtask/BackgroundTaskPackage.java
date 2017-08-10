package com.jamesisaac.rnbackgroundtask;

import android.app.Application;
import com.evernote.android.job.JobManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackgroundTaskPackage implements ReactPackage {
    /**
     * android-job needs to be set up with the Application context, so provide a convenience method
     * which users can add to the Application.onCreate.
     */
    public static void useContext(Application context) {
        JobManager.create(context).addJobCreator(new RNJobCreator());
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();

        modules.add(new BackgroundTaskModule(reactContext));

        return modules;
    }

    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}