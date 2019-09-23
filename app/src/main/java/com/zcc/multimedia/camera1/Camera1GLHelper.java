package com.zcc.multimedia.camera1;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zcc.multimedia.grafika.gles.EglCore;
import com.zcc.multimedia.grafika.gles.FullFrameRect;
import com.zcc.multimedia.grafika.gles.GlUtil;
import com.zcc.multimedia.grafika.gles.Texture2dProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class Camera1GLHelper implements GLSurfaceView.Renderer, Camera.PreviewCallback {

    private final Object MUTEX = new Object();
    private Camera1Manager mCamera1Manager;
    private int w = 0;
    private int h = 0;
    private int mCameraTextureId = 0;
    private int mFBOTextureId = 0;
    private FullFrameRect mCameraDisplay;
    private FullFrameRect mScreenDisplay;
    private SurfaceTexture mCameraSurfaceTexture;
    private EglCore mCameraEglCore;
    private Handler mMainHandler;
    private Activity mActivity;
    private GLSurfaceView mGLSurface;
    private boolean tobeInit = true;
    private boolean isOESFBOPrepared = false;
    private boolean permissionGranted = false;
    private boolean isInited = false;
    private boolean isUsingFBO = false;
    private boolean isShowFBO = false;
    private OnTextureRendListener onTextureRendListener;
    private int[] glFrameBuffer = new int[1];
    private int[] glRenderBuffer = new int[1];
    private int[] glFBOTexture = new int[1];
    private float[] matrixI = new float[16];

    public Camera1GLHelper(Activity activity, GLSurfaceView mGLSurface) {
        this.mCamera1Manager = new Camera1Manager();
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mActivity = activity;
        this.mGLSurface = mGLSurface;
        Matrix.setIdentityM(matrixI, 0);
    }

    public void changeFBO() {
        isUsingFBO = !isUsingFBO;
    }

    public void changeShowFBO() {
        isShowFBO = !isShowFBO;
    }

    public void setOnTextureRendListener(OnTextureRendListener onTextureRendListener) {
        this.onTextureRendListener = onTextureRendListener;
    }


    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        isInited = true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDisplay = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        mScreenDisplay = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
        mCameraTextureId = mCameraDisplay.createTextureObject();
        mFBOTextureId = mScreenDisplay.createTextureObject();
        mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);
//        mCameraEglCore = new EglCore(EGL14.eglGetCurrentContext(), EglCore.FLAG_RECORDABLE);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    private void prepareOESFrameBuffer(int w, int h) {
        if (isOESFBOPrepared) return;
        isOESFBOPrepared = true;
        GLES11Ext.glGenFramebuffersOES(1, glFrameBuffer, 0);
        GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES, glFrameBuffer[0]);

        GLES11Ext.glGenRenderbuffersOES(1, glRenderBuffer, 0);
        GLES11Ext.glBindRenderbufferOES(GLES11Ext.GL_RENDERBUFFER_OES, glRenderBuffer[0]);
        GLES11Ext.glRenderbufferStorageOES(GLES11Ext.GL_RENDERBUFFER_OES, GLES11Ext.GL_DEPTH_COMPONENT16_OES, w, h);
        GLES11Ext.glFramebufferRenderbufferOES(GLES11Ext.GL_FRAMEBUFFER_OES, GLES11Ext.GL_DEPTH_ATTACHMENT_OES,
                GLES11Ext.GL_RENDERBUFFER_OES, glRenderBuffer[0]);

        GLES11Ext.glFramebufferTexture2DOES(GLES11Ext.GL_FRAMEBUFFER_OES, GLES11Ext.GL_COLOR_ATTACHMENT0_OES,
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mFBOTextureId, 0);

        int status = GLES11Ext.glCheckFramebufferStatusOES(GLES11Ext.GL_FRAMEBUFFER_OES);
        if (status != GLES11Ext.GL_FRAMEBUFFER_COMPLETE_OES) {
            throw new RuntimeException("fbo oes failed");
        }

        GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES, 0);
    }

    private void createFBO(int width, int height) {
        if (isOESFBOPrepared) return;
        GLES20.glGenFramebuffers(1, glFrameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFrameBuffer[0]);
        GlUtil.checkGlError("bindFrameBuffer");

        GLES20.glGenTextures(1, glFBOTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glFBOTexture[0]);
        GlUtil.checkGlError("glBindTexture ");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height,
                0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GlUtil.checkGlError("glTexParameter");

        GLES20.glGenRenderbuffers(1, glRenderBuffer, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, glRenderBuffer[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, glRenderBuffer[0]);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, glFBOTexture[0], 0);
        GlUtil.checkGlError("glFramebufferTexture2D");
        // See if GLES is happy with all this.
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        // Switch back to the default framebuffer.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        // create fbo finished
        isOESFBOPrepared = true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        synchronized (MUTEX) {
            this.h = height;
            this.w = width;
            if (tobeInit) {
                this.mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCamera1Manager.initCamera(mActivity, w, h, mCameraSurfaceTexture,
                                Camera1GLHelper.this);
                        tobeInit = false;
                    }
                });
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
//        prepareOESFrameBuffer(this.w, this.h);
        createFBO(this.w, this.h);
        if (isInited) {
            float[] mtx = new float[16];
            mCameraSurfaceTexture.updateTexImage();
            mCameraSurfaceTexture.getTransformMatrix(mtx);
            if (isUsingFBO) {
//                GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES, glFrameBuffer[0]);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFrameBuffer[0]);
            }
            mCameraDisplay.drawFrame(mCameraTextureId, mtx);
            if (isUsingFBO) {
//                GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
            if (isShowFBO) {
                mScreenDisplay.drawFrame(mFBOTextureId, matrixI);
                int eroor = GLES20.glGetError();
                Log.e("camera1", eroor + "");
            }
            if (onTextureRendListener != null) {
                onTextureRendListener.onFrame(mCameraTextureId);
            }
        }
        mGLSurface.requestRender();
    }

    public void initCamera() {
        synchronized (MUTEX) {
            if (w != 0 && h != 0)
                mCamera1Manager.initCamera(mActivity, w, h, mCameraSurfaceTexture,
                        Camera1GLHelper.this);
            else
                tobeInit = true;
        }
    }

    private void drawTexture2D(int texture, float[] matrix) {


    }

    public interface OnTextureRendListener {
        void onFrame(int textureId);
    }
}
