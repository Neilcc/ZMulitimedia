package com.zcc.multimedia.camera1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zcc.multimedia.R;

public class Camera1Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Camera1Act";
    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
//    private static final String TEST_FILE_NAME = "test.mp4";
    private Button recordButton;
    private GLSurfaceView cameraGLSurface;

    private boolean isRecordingNow = false;
    private Camera1GLHelper camera1Helper;
//    private CapturingManager capturingManager;

    public static boolean isPermissionNotGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            boolean result = ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED;
            if (!result) return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1_main);
        cameraGLSurface = findViewById(R.id.sv_camera);
        recordButton = findViewById(R.id.btn_record);
        recordButton.setOnClickListener(this);
        initGLSurface();
        if (isPermissionNotGranted(this, PERMISSIONS)) {
            // 开启扫图
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION);
        } else {
            initCameraSurface();
        }
    }

    private void initGLSurface() {
        camera1Helper = new Camera1GLHelper(this, cameraGLSurface);
        camera1Helper.setOnTextureRendListener(textureId -> {
//                if (capturingManager != null && capturingManager.isStarted(0)) {
//                    capturingManager.captureFrame(textureId, 0);
//                }
        });
        cameraGLSurface.setEGLConfigChooser(8, 8, 8,
                8, 16, 0);
        cameraGLSurface.setZOrderMediaOverlay(true);
        cameraGLSurface.getHolder().setFormat(PixelFormat.RGBA_8888);
        cameraGLSurface.setZOrderOnTop(true);
        cameraGLSurface.setEGLContextClientVersion(2);
        cameraGLSurface.setRenderer(camera1Helper);
        cameraGLSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                isRecordingNow = doRecordStuff(isRecordingNow);
                break;
            default:
                Log.e(TAG, "no view id matched");
                break;
        }
    }

    private boolean doRecordStuff(boolean isRecordingNow) {
        if (isRecordingNow) {
            recordButton.setText(R.string.start_recording);
            cameraGLSurface.queueEvent(()->{

            });
            cameraGLSurface.queueEvent(() -> {
//                    capturingManager.stopCapturing(0);
            });
//            Toast.makeText(this,
//                    "record successed! file at " + CapturingManager.getDirectoryDCIM()
//                            + TEST_FILE_NAME, Toast.LENGTH_LONG).show();
        } else {
            recordButton.setText(R.string.stop_recording);
//            if (capturingManager == null) {
//                capturingManager = CapturingManager.getInstance();
//            }
            cameraGLSurface.queueEvent(() -> {
//                    capturingManager.initCapturing(camera1Helper.getW(), camera1Helper.getH(),
//                            CapturingManager.getDirectoryDCIM() + TEST_FILE_NAME, 0,
//                            Texture2dProgram.ProgramType.TEXTURE_EXT);
//                    capturingManager.startCapturing(0);
            });
        }
        return !isRecordingNow;
    }

    private void initCameraSurface() {
        cameraGLSurface.onResume();
        camera1Helper.initCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraGLSurface.onResume();
    }

    @Override
    protected void onPause() {
        cameraGLSurface.onPause();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            int count = 0;
            for (String r : permissions) {
                if (r.equals(Manifest.permission.CAMERA)) {
                    count++;
                }
                if (r.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    count++;
                }
                if (r.equals(Manifest.permission.RECORD_AUDIO)) {
                    count++;
                }
                if (count == permissions.length) {
                    initCameraSurface();
                }
            }
        }
    }
}
