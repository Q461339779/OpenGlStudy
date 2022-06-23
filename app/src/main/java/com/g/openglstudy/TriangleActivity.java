package com.g.openglstudy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 直角三角形
 */
public class TriangleActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tringle);
        glSurfaceView = findViewById(R.id.openglEs);
        init();
    }

    private void init() {
        //设置opengl上下文环境
        glSurfaceView.setEGLContextClientVersion(2);
        //GlSurfaceView设置渲染器
        glSurfaceView.setRenderer(new TriangleRender(this));
        //设置渲染模式
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


}