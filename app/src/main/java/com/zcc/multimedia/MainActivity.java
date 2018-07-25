package com.zcc.multimedia;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zcc.multimedia.activities.Camera1ActivityWithFBO;
import com.zcc.multimedia.activities.Camera1ActivityWithSurfaceTexture;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_camera1_with_surface_texture).setOnClickListener(this);
        findViewById(R.id.tv_camera1_with_fbo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera1_with_surface_texture:
                Intent intent = new Intent();
                intent.setClass(this.getApplicationContext(), Camera1ActivityWithSurfaceTexture.class);
                startActivity(intent);
                break;
            case R.id.tv_camera1_with_fbo:
                Intent intent1 = new Intent();
                intent1.setClass(this.getApplicationContext(), Camera1ActivityWithFBO.class);
                startActivity(intent1);
                break;

        }
    }
}
