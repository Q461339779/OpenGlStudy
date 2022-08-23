package com.g.openglstudy.model;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.g.openglstudy.R;
import com.g.openglstudy.audio.AudioCollector;
import com.g.openglstudy.util.CommonUtils;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static com.g.openglstudy.model.MyNativeRender.SAMPLE_TYPE;
import static com.g.openglstudy.model.MyNativeRender.SAMPLE_TYPE_3D_MODEL;
import static com.g.openglstudy.model.MyNativeRender.SAMPLE_TYPE_3D_MODEL_ANIM;

public class LoadModelActivity extends Activity implements AudioCollector.Callback, ViewTreeObserver.OnGlobalLayoutListener, SensorEventListener, View.OnClickListener, ModelMyGLRender.FPSListener{
    private ModelGLSurfaceView modelGLSurfaceView;
    private ModelMyGLRender modelMyGLRender = new ModelMyGLRender();
    private ViewGroup mRootView;
    private SensorManager mSensorManager;
    private AudioCollector mAudioCollector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_model);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        modelMyGLRender.init();
        modelMyGLRender.setOnFPSListener(this);


    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
        String fileDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        CommonUtils.copyAssetsDirToSDCard(this, "avata1", fileDir + "/model");
        CommonUtils.copyAssetsDirToSDCard(this, "avata2", fileDir + "/model");
        CommonUtils.copyAssetsDirToSDCard(this, "vampire", fileDir + "/model");
        //font related
        CommonUtils.copyAssetsDirToSDCard(this, "fonts", fileDir);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (mAudioCollector != null) {
            mAudioCollector.unInit();
            mAudioCollector = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modelMyGLRender.unInit();
        /*
         * Once the EGL context gets destroyed all the GL buffers etc will get destroyed with it,
         * so this is unnecessary.
         * */
    }

    @Override
    public void onGlobalLayout() {
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        modelGLSurfaceView = new ModelGLSurfaceView(this, modelMyGLRender);
        mRootView.addView(modelGLSurfaceView, 0, lp);
        modelGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
        modelMyGLRender.setParamsInt(SAMPLE_TYPE, SAMPLE_TYPE_3D_MODEL_ANIM, 0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:
                modelMyGLRender.setGravityXY(event.values[0], event.values[1]);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAudioBufferCallback(short[] buffer) {
        modelMyGLRender.setAudioData(buffer);
    }

    @Override
    public void onFpsUpdate(int fps) {

    }
}