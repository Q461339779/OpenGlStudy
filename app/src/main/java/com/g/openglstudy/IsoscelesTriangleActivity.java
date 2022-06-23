package com.g.openglstudy;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IsoscelesTriangleActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isosceles_triangle);
         glSurfaceView = (GLSurfaceView) findViewById(R.id.Isosceles);
        init();
    }

    private void init() {
        //设置opengl上下文环境
        glSurfaceView.setEGLContextClientVersion(2);
        //GlSurfaceView设置渲染器
        glSurfaceView.setRenderer(new IsoscelesTrangleRender( this));
        //设置渲染模式
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}