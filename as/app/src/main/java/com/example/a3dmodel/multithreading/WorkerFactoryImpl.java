package com.example.a3dmodel.multithreading;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class WorkerFactoryImpl extends WorkerFactory {
    private Runnable runnable;

    public WorkerFactoryImpl(){
    }

    public WorkerFactoryImpl(Runnable runnable){
        this.runnable = runnable;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext,
                                         @NonNull String workerClassName,
                                         @NonNull WorkerParameters workerParameters) {
        return new WorkerImpl(appContext, workerParameters, runnable);
    }
}
