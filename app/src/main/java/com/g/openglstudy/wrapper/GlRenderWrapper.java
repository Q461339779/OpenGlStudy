package com.g.openglstudy.wrapper;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.g.openglstudy.fliter.BeautifyFilter;
import com.g.openglstudy.fliter.CameraFilter;
import com.g.openglstudy.fliter.ScreenFilter;
import com.g.openglstudy.util.CameraHelper;
import com.g.openglstudy.widget.GlRenderView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GlRenderWrapper implements GLSurfaceView.Renderer
        , SurfaceTexture.OnFrameAvailableListener
        , CameraHelper.OnPreviewSizeListener
        , CameraHelper.OnPreviewListener {

    private final GlRenderView glRenderView;
    private CameraHelper camera2Helper;
    private int[] mTextures;
    private SurfaceTexture mSurfaceTexture;
    private CameraFilter cameraFilter;
    private ScreenFilter screenFilter;
    private boolean beautyEnable;
    private int mPreviewWdith;
    private int mPreviewHeight;
    private int screenX;
    private int screenY;
    private int screenSurfaceWid;
    private int screenSurfaceHeight;
    private float[] mtx = new float[16];
    private BeautifyFilter beaytyFilter;
    private String TAG = "GlRenderWrapper";
    //构造render 包装类
    public GlRenderWrapper(GlRenderView glRenderView) {
        Log.i("Magic-"+TAG,"构造GlRenderWrapper---2");
        this.glRenderView = glRenderView;
    }

    /**
     * 当新的数据帧可用时候
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.i("Magic-"+TAG,"onFrameAvailable---33");
        glRenderView.requestRender();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("Magic-"+TAG,"onSurfaceCreated---3");
        camera2Helper = new CameraHelper((Activity) glRenderView.getContext());

        mTextures = new int[1];
        //创建一个纹理
        GLES20.glGenTextures(mTextures.length, mTextures, 0);
        //将纹理和离屏buffer绑定
        //构造一个新的SurfaceTexture将图像流到给定的OpenGL纹理(摄像头数据与纹理关联)
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        //使用fbo 将samplerExternalOES 输入到sampler2D中
        cameraFilter = new CameraFilter(glRenderView.getContext());
        //负责将图像绘制到屏幕上
        screenFilter = new ScreenFilter(glRenderView.getContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //1080 1899
        Log.i("Magic-"+TAG,"onSurfaceChanged---12");
        camera2Helper.setPreviewSizeListener(this);
        camera2Helper.setOnPreviewListener(this);
        //打开相机
        camera2Helper.openCamera(width, height, mSurfaceTexture);
//        tracker = new FaceTracker("/sdcard/lbpcascade_frontalface.xml", "/sdcard/seeta_fa_v1.1.bin", camera2Helper);
//        tracker.startTrack();


        float scaleX = (float) mPreviewHeight / (float) width;
        float scaleY = (float) mPreviewWdith / (float) height;

        float max = Math.max(scaleX, scaleY);

        screenSurfaceWid = (int) (mPreviewHeight / max);
        //screenSurfaceWid = 3840;
        screenSurfaceHeight = (int) (mPreviewWdith / max);

        screenX = width - (int) (mPreviewHeight / max);
        screenY = height - (int) (mPreviewWdith / max);

        //prepare 传如 绘制到屏幕上的宽 高 起始点的X坐标 起使点的Y坐标
        cameraFilter.prepare(1920, screenSurfaceHeight, 0, screenY);
        screenFilter.prepare(1920, screenSurfaceHeight, 0, screenY);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onSurfaceDestory() {
        if (camera2Helper != null) {
            camera2Helper.closeCamera();
            camera2Helper.setPreviewSizeListener(null);
        }


        if (cameraFilter != null)
            cameraFilter.release();
        if (screenFilter != null)
            screenFilter.release();

//        tracker.stopTrack();
//        tracker = null;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i("Magic-"+TAG,"onDrawFrame---28");
        int textureId;
        // 配置屏幕
        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        GLES20.glClearColor(0, 0, 0, 0);
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //更新获取一张图
        mSurfaceTexture.updateTexImage();

        mSurfaceTexture.getTransformMatrix(mtx);
        //cameraFiler需要一个矩阵，是Surface和我们手机屏幕的一个坐标之间的关系
        cameraFilter.setMatrix(mtx);

        textureId = cameraFilter.onDrawFrame(mTextures[0]);
//        if (bigEyeEnable) {
//            bigeyeFilter.setFace(tracker.mFace);
//            textureId = bigeyeFilter.onDrawFrame(textureId);
//        }

        if (beautyEnable) {
            textureId = beaytyFilter.onDrawFrame(textureId);
        }

//        if (stickEnable) {
//            stickerFilter.setFace(tracker.mFace);
//            textureId = stickerFilter.onDrawFrame(textureId);
//        }

        int id = screenFilter.onDrawFrame(textureId);
    }



    @Override
    public void onSize(int width, int height) {
        Log.i("Magic-"+TAG,"onSize---20");
        mPreviewWdith = width;
        mPreviewHeight = height;
    }

    @Override
    public void onPreviewFrame(byte[] data, int len) {

    }

    public void enableBeauty(boolean isChecked) {
        Log.i("Magic-"+TAG,"enableBeauty");
        this.beautyEnable = isChecked;
        if (isChecked) {
            beaytyFilter = new BeautifyFilter(glRenderView.getContext());
            beaytyFilter.prepare(screenSurfaceWid, screenSurfaceHeight, screenX, screenY);

        } else {
            beaytyFilter.release();
            beaytyFilter = null;
        }
    }
}
