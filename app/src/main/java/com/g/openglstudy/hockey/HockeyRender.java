package com.g.openglstudy.hockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.g.openglstudy.R;
import com.g.openglstudy.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * 渲染类
 */
public class HockeyRender implements GLSurfaceView.Renderer {

    //每个顶点坐标 坐标数
    private static final int POSITION_COMPONENT_COUNT = 2;

    //float 数据所占用的字节数
    private static final int BYTES_PER_FLOAT = 4;
    //FloatBuffer 类型的顶点数据
    private  FloatBuffer vertexData;
    //顶点着色器代码
    private String vertexShaderSource;
    //片源着色器代码
    private String fragmentShaderSource;

    //坐标数组
    float[] tableVerticesWithTriangles = {
            // 第一个三角形
            0f, 0f,
            9f, 14f,
            0f, 14f,
            // 第二个三角形
            0f, 0f,
            9f, 0f,
            9f, 14f
    };

    public HockeyRender(Context context) {
        //读取顶点着色器信息
        vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        //读取片源着色器信息
        fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        vertexData = ByteBuffer
                //分配本地内存  不受jvm控制  属于系统级
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                // 设置为本地字节序
                .order(ByteOrder.nativeOrder())
                //创建为浮点类型字节缓冲区
                .asFloatBuffer();
        //将给定源浮点数组的全部内容传输到缓冲区
        vertexData.put(tableVerticesWithTriangles);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //定义填充屏幕的颜色
        GLES20.glClearColor(1.0f,0.0f,0.0f,0.0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        //设置显示窗口大小
        GLES20.glViewport(0,0,width,height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空屏幕 使用 glClearColor 定义的颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    }
}
