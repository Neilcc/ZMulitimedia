

package com.zcc.binarystar.core.executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DefaultTaskExecutor implements TaskExecutor {
    private final Object mLock = new Object();
    private ExecutorService subThreadExecutor = Executors.newFixedThreadPool(2);

    @Nullable
    private volatile Handler mMainHandler;

    @Override
    public void executeOnMainThread(Runnable command) {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        //noinspection ConstantConditions
        mMainHandler.post(command);
    }

    @Override
    public void executeOnSubThread(Runnable command) {
        subThreadExecutor.execute(command);
    }
}
