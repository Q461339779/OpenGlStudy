package com.g.openglstudy;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TriangleRender implements GLSurfaceView.Renderer {
    /**
     * attribute 表示从外部传入
     * vec4 n维浮点数向量 四个float数据
     * gl_Position = vPosition; gl_Position内置变量
     * varying vec2 aCoord;//顶点着色器的输出变量  片元着色器的输入变量
     * attribute vec2 vCoord;  //纹理坐标
     * 世界坐标  0,0 中心 纹理坐标0,0左下
     * uniform samplerExternalOES  vTexture;贴图 采样器 uniform表示外部传入变量(片源着色器)
     * texture2D(vTexture,aCoord)
     * texture2D: vTexture采样器，采样  aCoord 这个像素点的RGBA值
     */

    private Context context;
    private FloatBuffer vertexBuffer;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    private int mPositionHandle;
    private int mColorHandle;
    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节
    //设置颜色，依次为红绿蓝和透明通道
    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    /**
     *初始化自己数组
     * @param context
     */
    public TriangleRender(Context context) {
        this.context = context;

        //在 JVM 以外申请系统级内存    省去了复制内存到JVM
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        //设置 ByteBuffer 字节序 ;ByteOrder.nativeOrder() 返回硬件平台的字节序列
        byteBuffer.order(ByteOrder.nativeOrder());
        //创建Float型缓冲区
        vertexBuffer = byteBuffer.asFloatBuffer();
        //将顶点数据放入buffer中
        vertexBuffer.put(triangleCoords);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //设置缓冲位置起始位置
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);//创建窗口，第一二个参数为窗口坐标，三四参数宽高
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    public int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        //要被替换源代码的着色器对象的句柄（ID）。
        //shaderCode指定指向包含要加载到着色器的源代码的字符串的指针数组。
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
