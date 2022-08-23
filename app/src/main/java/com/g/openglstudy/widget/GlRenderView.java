package com.g.openglstudy.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;



import com.g.openglstudy.wrapper.GlRenderWrapper;


public class GlRenderView  extends GLSurfaceView {


    private String TAG = "GlRenderView";
    private final GlRenderWrapper glRender;

    public GlRenderView(Context context) {
        this(context,null);
        Log.i("Magic-"+TAG,"构造初始化View");
    }

    //1 构造 view 设置 OpenGL 环境
    public GlRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("Magic-"+TAG,"布局初始化View---1");
        //设置openGL版本
        setEGLContextClientVersion(2);
        //创建渲染器
        glRender = new GlRenderWrapper(this);
        //设置渲染器
        setRenderer(glRender);
        //设置手动渲染模式  当数据可用时候渲染
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        glRender.onSurfaceDestory();
    }

    public void enableBeauty(final boolean isChecked) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                glRender.enableBeauty(isChecked);
            }
        });
    }
}
