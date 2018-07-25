package com.zcc.multimedia.activities;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.zcc.multimedia.R;
import com.zcc.multimedia.egl.EGLHandlerThread;
import com.zcc.multimedia.gles.GLESHelper;
import com.zcc.multimedia.grafika.gles.GlUtil;

public class Camera1ActivityWithFBO extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {

    private SurfaceView mSurfaceView;
    private EGLHandlerThread mSurfaceThread;

    private int[] glTexture = new int[1];
    private int[] glFBO = new int[1];
    private int[] glRenderBuffer = new int[1];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceThread = new EGLHandlerThread();
        mSurfaceThread.start();
        setContentView(R.layout.activity_fbo);
        mSurfaceView = findViewById(R.id.sv_fbo);
        mSurfaceView.getHolder().addCallback(this);
        findViewById(R.id.btn_creat_fbo).setOnClickListener(this);
        findViewById(R.id.btn_show_fbo).setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        mSurfaceThread.quit();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceThread.waitUntilReady();
        mSurfaceThread.creatSurface(holder.getSurface());
        mSurfaceThread.getHandler().post(() -> GLESHelper.rend(width, height));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void createFBO() {
        GLES20.glGenTextures(1,glTexture, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glTexture[0]);
        GlUtil.checkGlError("glBindTexture ");
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GlUtil.checkGlError("glTexParameter");

        GLES20.glGenRenderbuffers(1, glRenderBuffer , 0);
        GLES20.glGenFramebuffers(1, glFBO, 0);

    }

    private void showFBO() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_creat_fbo:
                mSurfaceThread.getHandler().post(this::createFBO);
                break;
            case R.id.btn_show_fbo:
                mSurfaceThread.getHandler().post(this::showFBO);
                break;
        }
    }
}
