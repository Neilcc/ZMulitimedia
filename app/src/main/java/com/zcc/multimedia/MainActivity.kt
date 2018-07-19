package com.zcc.multimedia

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import com.zcc.multimedia.camera1.Camera1Activity
import com.zcc.multimedia.camera2.Camera2Fragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var container: FrameLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container = findViewById(R.id.fragment_container)
        findViewById<View>(R.id.camera2).setOnClickListener(this)
        findViewById<View>(R.id.tv_camera1).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.camera2 -> supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, Camera2Fragment.newInstance(), "camera2")
                    .addToBackStack("camera2")
                    .commitAllowingStateLoss()
            R.id.tv_camera1 -> {
                val intent = Intent()
                intent.setClass(this, Camera1Activity::class)
                startActivity(intent)
            }
        }
    }
}

