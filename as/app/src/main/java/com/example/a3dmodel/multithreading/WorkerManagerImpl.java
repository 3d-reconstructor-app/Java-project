package com.example.a3dmodel.multithreading;
import android.app.Application;

import androidx.work.WorkManager;

public class WorkerManagerImpl  {
    Application application = null;

    public WorkerManagerImpl(Application application){
        this.application = application;
    }

    private final WorkManager workManager =  WorkManager.getInstance(application.getApplicationContext());




}
