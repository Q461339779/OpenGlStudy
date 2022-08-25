package com.g.openglstudy.model;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.g.openglstudy.model.MyNativeRender.SAMPLE_TYPE;
import static com.g.openglstudy.model.MyNativeRender.SAMPLE_TYPE_SET_GRAVITY_XY;
import static com.g.openglstudy.model.MyNativeRender.SAMPLE_TYPE_SET_TOUCH_LOC;


public class ModelMyGLRender implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLRender";
    private MyNativeRender mNativeRender;
    private int mSampleType;
    private int frameCount = 0;
    private long firstCheckTime = 0;

    ModelMyGLRender() {
        mNativeRender = new MyNativeRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mNativeRender.native_OnSurfaceCreated();
        Log.e(TAG, "onSurfaceCreated() called with: GL_VERSION = [" + gl.glGetString(GL10.GL_VERSION) + "]");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mNativeRender.native_OnSurfaceChanged(width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mNativeRender.native_OnDrawFrame();
        checkFPS();

    }

    private void checkFPS() {

        if(frameCount == 0) {
            firstCheckTime = System.currentTimeMillis();
        } else if(frameCount == 60) {
            long currentTimeMillis = System.currentTimeMillis();
            long timeGap = currentTimeMillis - firstCheckTime;
            int fps = (int)(frameCount* 1000.0f / timeGap);
            frameCount = 0;
            firstCheckTime = currentTimeMillis;

            if(mFPSListener != null) {
                mFPSListener.onFpsUpdate(fps);
            }
        }
        frameCount++;
    }

    private FPSListener mFPSListener;
    public void setOnFPSListener(FPSListener fpsListener) {
        mFPSListener = fpsListener;
    }

    public interface FPSListener {
        void onFpsUpdate(int fps);
    }

    public void init() {
        mNativeRender.native_Init();
    }

    public void unInit() {
        mNativeRender.native_UnInit();
    }

    public void setParamsInt(int paramType, int value0, int value1) {
        if (paramType == SAMPLE_TYPE) {
            mSampleType = value0;
        }
        mNativeRender.native_SetParamsInt(paramType, value0, value1);
    }

    public void setTouchLoc(float x, float y)
    {
        mNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_TOUCH_LOC, x, y);
    }

    public void setGravityXY(float x, float y) {
        mNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_GRAVITY_XY, x, y);
    }

    public void setImageData(int format, int width, int height, byte[] bytes) {
        mNativeRender.native_SetImageData(format, width, height, bytes);
    }

    public void setImageDataWithIndex(int index, int format, int width, int height, byte[] bytes) {
        mNativeRender.native_SetImageDataWithIndex(index, format, width, height, bytes);
    }

    public void setAudioData(short[] audioData) {
        mNativeRender.native_SetAudioData(audioData);
    }

    public int getSampleType() {
        return mSampleType;
    }

    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY)
    {
        mNativeRender.native_UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
    }

}