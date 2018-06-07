package com.zcc.multimedia.egl;

import android.opengl.GLES30;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author zhuchengcheng
 * reference https://zhangtemplar.github.io/pbo/
 */

public class PixelBufferHelper {
    private int mWidth;
    private int mHeight;
    private IntBuffer mPboIds;
    private int mPboSize;

    public void init(int width, int height) {
        this.mHeight = height;
        this.mWidth = width;
        mPboSize = mHeight * mWidth;
    }

    public void initPBO() {
        mPboIds = IntBuffer.allocate(2);
//生成2个PBO
        GLES30.glGenBuffers(2, mPboIds);

//绑定到第一个PBO
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPboIds.get(0));
//设置内存大小
        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, mPboSize, null, GLES30.GL_STATIC_READ);

//绑定到第而个PBO
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPboIds.get(1));
//设置内存大小
        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, mPboSize, null, GLES30.GL_STATIC_READ);

//解除绑定PBO
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ByteBuffer bindPixelBuffer() {
        //绑定到第一个PBO
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPboIds.get(0));
        GLES30.glReadPixels(0, 0, mWidth, mHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, 0);

        //绑定到第二个PBO
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, mPboIds.get(1));

        //glMapBufferRange会等待DMA传输完成，所以需要交替使用pbo
        //映射内存
        ByteBuffer byteBuffer = (ByteBuffer) GLES30.glMapBufferRange(GLES30.GL_PIXEL_PACK_BUFFER, 0, mPboSize, GLES30.GL_MAP_READ_BIT);

        //解除映射
        GLES30.glUnmapBuffer(GLES30.GL_PIXEL_PACK_BUFFER);
        unbindPixelBuffer();

        //交给mRecordHelper录制
//        mRecordHelper.onRecord(byteBuffer, mInputWidth, mInputHeight, mRowStride, mLastTimestamp);
        return byteBuffer;
    }

    //解绑pbo
    private void unbindPixelBuffer() {
        //解除绑定PBO
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);

        //交换索引
//        mPboIndex = (mPboIndex + 1) % 2;
//        mPboNewIndex = (mPboNewIndex + 1) % 2;
    }
}
