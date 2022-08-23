package com.g.openglstudy.hockey;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.g.openglstudy.R;
import com.g.openglstudy.util.ShaderHelper;
import com.g.openglstudy.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 渲染类
 */
public class HockeyRender implements GLSurfaceView.Renderer {


    private static final int COLOR_COMPONENT_COUNT = 3;
    //每个顶点坐标 坐标数
    private static final int POSITION_COMPONENT_COUNT = 2;
    //float 数据所占用的字节数
    private static final int BYTES_PER_FLOAT = 4;

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    //private static final String U_COLOR = "u_Color";
    //private int uColorLocation;
    //片段常量
    private static final String A_COLOR = "a_Color";
    //保存color位置的变量
    private int aColorLocation;

    //FloatBuffer 类型的顶点数据
    private FloatBuffer vertexData;
    //顶点着色器代码
    private String vertexShaderSource;
    //片源着色器代码
    private String fragmentShaderSource;

    /*//片段常量
    private static final String U_COLOR = "u_Color";
    //保存color位置的变量
    private int uColorLocation;*/
    //顶点常量
    private static final String A_POSITION = "a_Position";
    //顶点位置变量
    private int aPositionLocation;


    //坐标数组
    float[] tableVerticesWithTriangles = {
            /* // 第一个三角形
             0f, 0f,
             9f, 14f,
             0f, 14f,
             // 第二个三角形
             0f, 0f,
             9f, 0f,
             9f, 14f*/

            /* // 第一个三角形
             -0.5f, -0.5f,
             0.5f, 0.5f,
             -0.5f, 0.5f,
             // 第二个三角形
             -0.5f, -0.5f,
             0.5f, -0.5f,
             0.5f, 0.5f,
             // 中间的分界线
             -0.5f, 0f,
             0.5f, 0f,
             // 两个木槌的质点位置
             0f, -0.25f,
             0f, 0.25f*/


            /*  0,     0,
              -0.5f, -0.5f,
              0.5f, -0.5f,
              0.5f,  0.5f,
              -0.5f,  0.5f,
              -0.5f, -0.5f,
              // 中间的分界线
              -0.5f, 0f,
              0.5f, 0f,
              // 两个木槌的质点位置
              0f, -0.25f,
              0f, 0.25f
  */

            //  X, Y,        R, G, B
            // 三角扇形
            0, 0, 1f, 1f, 1f,
            -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            // 中间的分界线
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0f, 1f, 0f, 0f,
            // 两个木槌的质点位置
            0f, -0.25f, 0f, 0f, 1f,
            0f, 0.25f, 1f, 0f, 0f,


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
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        int programId = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(programId);
        //获取位置
        //uColorLocation = GLES20.glGetUniformLocation(programId, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);
        vertexData.position(0);
        //告诉OpenGL到哪里找到属性a_Position对应的数据
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false, 0, vertexData);
        //在开始绘制之前使用顶点数据
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //int programId = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(programId);
        // uColorLocation = GLES20.glGetUniformLocation(programId, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR);


        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);


        vertexData.position(2);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置显示窗口大小
        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空屏幕 使用 glClearColor 定义的颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //更新着色器中 u_Color 的颜色

      /*  //绘制三角形
        GLES20.glUniform4f(uColorLocation, 1.0f,1.0f,1.0f,1.0f);
        //第一个参数告诉OpenGL我们要画三角形；
        // 第二个参数0，告诉OpenGL从顶点数组（tableVerticesWithTriangles ）的开头处开始读顶点；
        // 第三个参数6，告诉OpenGL读入六个顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);*/

        //绘制扇形
//        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
//
//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
//
//        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
//
//        GLES20.glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);


    }
}
