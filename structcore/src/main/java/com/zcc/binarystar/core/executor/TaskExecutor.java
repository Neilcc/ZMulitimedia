

package com.zcc.binarystar.core.executor;

/**
 */
public interface TaskExecutor {
    public void executeOnMainThread(Runnable command);

    public void executeOnSubThread(Runnable command);
}
