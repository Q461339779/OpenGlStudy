package com.g.openglstudy;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class IsoscelesTrangleRender implements GLSurfaceView.Renderer {
    /**
     * attribute 表示从外部传入
     * vec4 n维浮点数向量 四个float数据
     * gl_Position = vPosition; gl_Position内置变量
     * varying vec2 aCoord;//顶点着色器传递数据到片源着色器
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
                    "uniform mat4 vMatrix;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
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

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];


    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    public IsoscelesTrangleRender(Context context) {
        this.context = context;

        //在 JVM 以外申请系统级内存    省去了复制内存到JVM
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        //设置 ByteBuffer 字节序 ;ByteOrder.nativeOrder() 返回硬件平台的字节序列
        byteBuffer.order(ByteOrder.nativeOrder());
        //创建Float型缓冲区
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //设置缓冲位置
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

        //局部坐标是对象相对于局部原点的坐标，也是物体起始的坐标。
        //下一步是将局部坐标变换为世界空间坐标，世界空间坐标是处于一个更大的空间范围的。这些坐标相对于世界的全局原点，它们会和其它物体一起相对于世界的原点进行摆放。
        //接下来我们将世界坐标变换为观察空间坐标，使得每个坐标都是从摄像机或者说观察者的角度进行观察的。
        //坐标到达观察空间之后，我们需要将其投影到裁剪坐标。裁剪坐标会被处理至-1.0到1.0的范围内，并判断哪些顶点将会出现在屏幕上。
        //最后，我们将裁剪坐标变换为屏幕坐标，我们将使用一个叫做视口变换(Viewport Transform)的过程。视口变换将位于-1.0到1.0范围的坐标变换到由glViewport函数所定义的坐标范围内。最后变换出来的坐标将会送到光栅器，将其转化为片段。
        //https://learnopengl-cn.github.io/img/01/08/coordinate_systems.png

        //设置相机和投影，获取相机矩阵和投影矩阵，然后用相机矩阵与投影矩阵相乘，得到实际变换矩阵
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        /**
         * https://blog.csdn.net/kongbaidepao/article/details/106081092
         * 透视投影
         * 平截头体进平面的四个值 l,R,B,T
         */
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置

        /**
         *
         * https://blog.csdn.net/wzy901213/article/details/89374121
         *
         * @param rm returns the result
         * @param rmOffset index into rm where the result matrix starts
         * @param eyeX eye point X eyex,eyey,eyez 代表相机在坐标轴的位置
         * @param eyeY eye point Y
         * @param eyeZ eye point Z
         * @param centerX center of view X centerX,centerY,centerZ代表物体的中心坐标
         * @param centerY center of view Y
         * @param centerZ center of view Z
         * @param upX up vector X upX,upY,upZ代表相机往哪边看
         * @param upY up vector Y
         * @param upZ up vector Z
         *
         */
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
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
