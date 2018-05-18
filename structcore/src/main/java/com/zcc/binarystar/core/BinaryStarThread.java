package com.zcc.binarystar.core;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Hengyun on 09/05/2017.
 */

public abstract class BinaryStarThread extends Thread {

    protected Handler mH;

    public BinaryStarThread(Handler mH) {
        this.mH = mH;
    }

    public void run() {
        Looper.prepare();
        super.run();
        Looper.loop();
    }

    public void bindWork(){

    }

}
