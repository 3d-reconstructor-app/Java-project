package com.example.a3dmodel.multithreading;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class WorkerImpl extends ListenableWorker {
    private static final Executor executor = Executors.newCachedThreadPool();
    private final  Runnable runnable;
    private static final String TAG = "WorkerImpl";

    public WorkerImpl(@NonNull Context context,
                      @NonNull WorkerParameters workerParams,
                      @NotNull Runnable runnable) {
        super(context, workerParams);

        this.runnable = runnable;
    }

    public static void execTask(Runnable runnable){
        executor.execute(runnable);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        return CallbackToFutureAdapter.getFuture(completer -> {
            Log.i(TAG, "Starting WorkerImpl");
//            setForegroundAsync(getForegroundInfoAsync());
            try {

                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return completer.set(Result.success());
        });

    }



}
