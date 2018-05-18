package com.zcc.binarystar.core;

import com.zcc.binarystar.core.executor.BinaryTaskExecutor;

/**
 * Created by cc on 2018/3/19.
 */

public abstract class ProgressCommand<T, SR, FR, U> extends Command<T, SR, FR> {

    protected IOnProgressCallBack<U> onProgressCallback;

    public void execute(T input, ICommandResp<SR, FR> iCommandResp, IOnProgressCallBack<U> onProgressCallback) {
        this.onProgressCallback = new ProgressCallbackWrapper<>(onProgressCallback);
        execute(input, iCommandResp);
    }

    protected IOnProgressCallBack<U> getProgressCallback() {
        return this.onProgressCallback;
    }

    public interface IOnProgressCallBack<U> {
        void onProgress(U data);
    }

    public static class ProgressCallbackWrapper<U> implements IOnProgressCallBack<U> {

        IOnProgressCallBack<U> iOnProgressCallBack;

        public ProgressCallbackWrapper(IOnProgressCallBack<U> iOnProgressCallBack) {
            this.iOnProgressCallBack = iOnProgressCallBack;
        }

        @Override
        public void onProgress(final U data) {
            if (BinaryTaskExecutor.isMainThread()) {
                iOnProgressCallBack.onProgress(data);
                return;
            }
            BinaryTaskExecutor.getInstance().executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    iOnProgressCallBack.onProgress(data);
                }
            });
        }
    }

}
