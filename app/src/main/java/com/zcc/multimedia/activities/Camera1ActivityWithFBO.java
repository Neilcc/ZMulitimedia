package com.zcc.multimedia.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.zcc.multimedia.R;
import com.zcc.multimedia.egl.EGLHandlerThread;
import com.zcc.multimedia.gles.GLESHelper;

public class Camera1ActivityWithFBO extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {

    private SurfaceView mSurfaceView;
    private EGLHandlerThread mSurfaceThread;

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
