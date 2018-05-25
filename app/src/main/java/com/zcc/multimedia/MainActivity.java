package com.zcc.multimedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.zcc.multimedia.camera2.Camera2Fragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.fragment_container);
        findViewById(R.id.camera2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, Camera2Fragment.newInstance(), "camera2")
                        .addToBackStack("camera2")
                        .commitAllowingStateLoss();
                break;
        }
    }
}
