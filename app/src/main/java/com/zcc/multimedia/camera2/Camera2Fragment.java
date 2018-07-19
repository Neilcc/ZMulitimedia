package com.zcc.multimedia.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zcc.multimedia.R;
import com.zcc.multimedia.databinding.FragmentCamera2Binding;
import com.zcc.multimedia.utils.PermissionUtil;

public class Camera2Fragment extends Fragment {

    private static final int PERMISION_REQ_CODE = 101;
    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };
    private FragmentCamera2Binding fragmentCamera2Binding;
    private CameraPresenter cameraPresenter;
    private int textureWidth;
    private int textureHeight;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            textureWidth = width;
            textureHeight = height;
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public static Camera2Fragment newInstance() {
        return new Camera2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCamera2Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera2, container, false);
        return fragmentCamera2Binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void openCamera() {
        if (!PermissionUtil.hasPermissionsGranted(getActivity(), VIDEO_PERMISSIONS)) {
            requestPermissions(VIDEO_PERMISSIONS, PERMISION_REQ_CODE);
        }else {
            final Activity activity = getActivity();
            if (null == activity || activity.isFinishing()) {
                return;
            }
            cameraPresenter.openCamera(getActivity(), textureWidth, textureHeight,
                    fragmentCamera2Binding.autoFitTextureView);
        }

    }

    private void init() {
        cameraPresenter = new CameraPresenter();
//        fragmentCamera2Binding.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISION_REQ_CODE) {
            if (grantResults.length == VIDEO_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this.getContext(), "permission failed !", Toast.LENGTH_LONG).show();
//                        ErrorDialog.newInstance(getString(R.string.permission_request))
//                                .show(getChildFragmentManager(), FRAGMENT_DIALOG);
                        break;
                    }
                }
            } else {
                Toast.makeText(this.getContext(), "permission failed !", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            cameraPresenter.openCamera(getActivity(), textureWidth, textureHeight, fragmentCamera2Binding.autoFitTextureView);
        }
    }
}
