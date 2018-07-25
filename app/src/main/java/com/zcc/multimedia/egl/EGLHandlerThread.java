package com.zcc.multimedia.egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;


public class EGLHandlerThread extends Thread {

    protected final Object MUTEX = new Object();
    protected EGLHandler mHandler;
    protected Looper mLooper;
    protected boolean isReady = false;

    protected EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    protected EGLConfig mEGLConfig;
    protected EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    protected EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

    public EGLHandlerThread() {
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        Looper.prepare();
        mLooper = Looper.myLooper();
        mHandler = new EGLHandler(mLooper);
        prepareEGL();
        synchronized (MUTEX) {
            isReady = true;
            MUTEX.notifyAll();
        }
        Looper.loop();
        destroyEGL();
    }

    private void destroyEGL() {
        EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void quit() {
        mLooper.quitSafely();
    }

    private void prepareEGL() {
        // 获取显示设备(默认的显示设备)
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        // 初始化
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        // 获取FrameBuffer格式和能力
        int[] configAttribs = {
                EGL14.EGL_BUFFER_SIZE, 32,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                // 总是以这个结尾，表示EOF
                EGL14.EGL_NONE
        };
        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!EGL14.eglChooseConfig(mEGLDisplay, configAttribs, 0, configs, 0, configs.length, numConfigs, 0)) {
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        mEGLConfig = configs[0];
        // 创建OpenGL上下文(可以先不设置EGLSurface，但EGLContext必须创建，
        // 因为后面调用GLES方法基本都要依赖于EGLContext)
        // 创建上下文比较简单，永远制定一下版本号就好
        int[] contextAttribs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                // 总是以这个结尾，表示EOF
                EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, mEGLConfig, EGL14.EGL_NO_CONTEXT, contextAttribs, 0);
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        // 设置默认的上下文环境和输出缓冲区(小米4上如果不设置有效的eglSurface后面创建着色器会失败，可以先创建一个默认的eglSurface)
        //EGL14.eglMakeCurrent(eglDisplay, surface.eglSurface, surface.eglSurface, eglContext);
        EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, mEGLContext);
    }

    public void enqueueRunnable(Runnable runnable) {
        if (mHandler != null)
            mHandler.post(runnable);
        else
            throw new EGLHandlerThreadException("thread not ready");
    }

    public void waitUntilReady() {
        synchronized (MUTEX) {
            while (!isReady) {
                try {
                    MUTEX.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void innerCreateSurface(Surface surface) {
        final int[] surfaceAttribs = {EGL14.EGL_NONE};
        mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surface, surfaceAttribs, 0);
        EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
    }

    public void creatSurface(Surface surface) {
        mHandler.post(() -> innerCreateSurface(surface));
    }

    public static class EGLHandlerThreadException extends RuntimeException {

        public EGLHandlerThreadException(String message) {
            super(message);
        }
    }

    private class EGLHandler extends Handler {

        public EGLHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
        }
    }
}
