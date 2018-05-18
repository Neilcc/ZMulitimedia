

package com.zcc.binarystar.core.executor;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;


public class BinaryTaskExecutor implements TaskExecutor {
    private static volatile BinaryTaskExecutor sInstance;
    @NonNull
    private static final Executor sMainThreadExecutor = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            getInstance().executeOnMainThread(command);
        }
    };
    @NonNull
    private static final Executor sSubThreadExecutor = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            getInstance().executeOnSubThread(command);
        }
    };
    @NonNull
    private TaskExecutor mDelegate;
    @NonNull
    private TaskExecutor mDefaultTaskExecutor;

    private BinaryTaskExecutor() {
        mDefaultTaskExecutor = new DefaultTaskExecutor();
        mDelegate = mDefaultTaskExecutor;
    }

    /**
     * Returns an instance of the task executor.
     *
     * @return The singleton BinaryTaskExecutor.
     */
    public static BinaryTaskExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (BinaryTaskExecutor.class) {
            if (sInstance == null) {
                sInstance = new BinaryTaskExecutor();
            }
        }
        return sInstance;
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    public void setDelegate(@Nullable TaskExecutor taskExecutor) {
        mDelegate = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    public Executor getMainThreadExecutor() {
        return sMainThreadExecutor;
    }

    public Executor getSubThreadExecutor() {
        return sSubThreadExecutor;
    }

    @Override
    public void executeOnMainThread(Runnable command) {
        mDelegate.executeOnMainThread(command);
    }

    @Override
    public void executeOnSubThread(Runnable command) {
        mDelegate.executeOnSubThread(command);
    }
}
