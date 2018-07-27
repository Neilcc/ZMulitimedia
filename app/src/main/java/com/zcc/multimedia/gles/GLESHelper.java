package com.zcc.multimedia.gles;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLESHelper {

    // 顶点着色器的脚本
    private static final String VERTICES_SHADER_SOURCE
            = "attribute vec2 vPosition;            \n" // 顶点位置属性vPosition
            + "void main(){                         \n"
            + "   gl_Position = vec4(vPosition,0,1);\n" // 确定顶点位置
            + "}";
    // 片元着色器的脚本
    private static final String FRAGMENT_SHADER_SOURCE
            = "precision mediump float;         \n" // 声明float类型的精度为中等(精度越高越耗资源)
            + "uniform vec4 uColor;             \n" // uniform的属性uColor
            + "void main(){                     \n"
            + "   gl_FragColor = uColor;        \n" // 给此片元的填充色
            + "}";

    public static int loadShader(int type, String source) {
        int shader = 0;
        shader = GLES20.glCreateShader(type);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int compiled[] = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e("error", "error");
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int creatProgram(int vertexShader, int fragmentShader) {
        int program = 0;
        program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("error", "error");
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    public static FloatBuffer getVertices() {
        float vertices[] = {
                0.0f, 0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f
        };
        ByteBuffer buffer = ByteBuffer.allocateDirect(vertices.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = buffer.asFloatBuffer();
        floatBuffer.put(vertices);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static void rend(int mProgram, int w, int h) {

        int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int uColor = GLES20.glGetUniformLocation(mProgram, "uColor");
        GLES20.glClearColor(0.0f, 0, 1.0f, 1.0f);
        GLES20.glViewport(0, 0, w, h);
        FloatBuffer floatBuffer = getVertices();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        // stride 步长
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, floatBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glUniform4f(uColor, 0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
    }

    public static int init() {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTICES_SHADER_SOURCE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_SOURCE);
        int mProgram = creatProgram(vertexShader, fragmentShader);
        return mProgram;
    }
}
