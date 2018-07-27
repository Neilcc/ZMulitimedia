package com.zcc.multimedia.activities;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Choreographer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.zcc.multimedia.R;
import com.zcc.multimedia.egl.EGLHandlerThread;
import com.zcc.multimedia.gles.GLESHelper;
import com.zcc.multimedia.grafika.gles.FullFrameRect;
import com.zcc.multimedia.grafika.gles.GlUtil;
import com.zcc.multimedia.grafika.gles.Texture2dProgram;

public class Camera1ActivityWithFBO extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener, Choreographer.FrameCallback {

    private SurfaceView mSurfaceView;
    private EGLHandlerThread mSurfaceThread;

    private boolean isFBOCreated = false;
    private int width = 100;
    private int height = 100;

    private int[] glFBOTexture = new int[1];
    private int[] glFBO = new int[1];
    private int[] glRenderBuffer = new int[1];
    private boolean enableFBO = false;
    private boolean drainTextureFromFBO = false;
    private FullFrameRect mFullScreen;
    private float[] matrix = new float[16];
    private int mProgram = 0;

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
        findViewById(R.id.btn_swap_rend_to_fbo).setOnClickListener(this);
        Matrix.setIdentityM(matrix, 0);
    }


    @Override
    protected void onDestroy() {
        mSurfaceThread.quit();
        GLES20.glDeleteFramebuffers(1, glFBO, 0);
        GLES20.glDeleteRenderbuffers(1, glRenderBuffer, 0);
        GLES20.glDeleteTextures(1, glFBOTexture, 0);
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceThread.waitUntilReady();
        mSurfaceThread.creatSurface(holder.getSurface());
        this.width = width;
        this.height = height;
        mSurfaceThread.enqueueRunnable(() -> mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D)));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    private void createFBO(int width, int height) {
        if (isFBOCreated) return;
        GLES20.glGenFramebuffers(1, glFBO, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFBO[0]);
        GlUtil.checkGlError("bindFrameBuffer");

        GLES20.glGenTextures(1, glFBOTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glFBOTexture[0]);
        GlUtil.checkGlError("glBindTexture ");
//        IntBuffer pixel = IntBuffer.allocate(1024 * 768);
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
        isFBOCreated = true;
    }

    private void showFBO() {
        drainTextureFromFBO = !drainTextureFromFBO;
    }

    private void rendToFBO() {
        enableFBO = !enableFBO;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_creat_fbo:
                mSurfaceThread.getHandler().post(() -> createFBO(this.width, this.height));
                break;
            case R.id.btn_show_fbo:
                mSurfaceThread.getHandler().post(this::showFBO);
                break;
            case R.id.btn_swap_rend_to_fbo:
                mSurfaceThread.enqueueRunnable(this::rendToFBO);
                break;
        }
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (mSurfaceThread != null) {
            mSurfaceThread.enqueueRunnable(() -> {
                GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
                GLES20.glViewport(0, 0, this.width, this.height);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                GlUtil.checkGlError("error");
                if (enableFBO) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glFBO[0]);
                }
                if (mProgram == 0) mProgram = GLESHelper.init();
                GLESHelper.rend(mProgram, this.width, this.height);
                if (enableFBO) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                }
                if (drainTextureFromFBO) {
                    mFullScreen.drawFrame(glFBOTexture[0], matrix);
                }
            });
        }
        Choreographer.getInstance().postFrameCallback(this);
    }
}
