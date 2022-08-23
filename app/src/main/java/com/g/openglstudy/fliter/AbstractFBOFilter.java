package com.g.openglstudy.fliter;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.g.openglstudy.util.OpenGlUtils;


public class AbstractFBOFilter extends BaseFilter {


    protected int[] mFrameBuffers;
    protected int[] mFBOTextures;
    private static final String TAG = "AbstractFBOFilter";
    public AbstractFBOFilter(Context mContext, int mVertexShaderId, int mFragShaderId) {
        super(mContext, mVertexShaderId, mFragShaderId);
        Log.i("Magic-"+TAG,"AbstractFBOFilter---9");
    }

    @Override
    protected void resetCoordinate() {
        Log.i("Magic-"+TAG,"resetCoordinate");
        mGlTextureBuffer.clear();
        float[] TEXTURE = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };
        mGlTextureBuffer.put(TEXTURE);
    }


    @Override
    public void prepare(int width, int height,int x,int y) {
        super.prepare(width, height,x,y);
        Log.i("Magic-"+TAG,"prepare---22");
        loadFOB();

    }

    private void loadFOB() {
        Log.i("Magic-"+TAG,"loadFOB---24");
        if (mFrameBuffers != null) {
            destroyFrameBuffers();
        }
        //创建FrameBuffer
        mFrameBuffers = new int[1];
        GLES20.glGenFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
        //创建FBO中的纹理
        mFBOTextures = new int[1];
        OpenGlUtils.glGenTextures(mFBOTextures);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFBOTextures[0]);
        //指定FBO纹理的输出图像的格式 RGBA
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mOutputWidth, mOutputHeight,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

        //将fbo绑定到2d的纹理上
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFBOTextures[0], 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

    }

    public void destroyFrameBuffers() {
        Log.i("Magic-"+TAG,"destroyFrameBuffers");
        //删除fbo的纹理
        if (mFBOTextures != null) {
            GLES20.glDeleteTextures(1, mFBOTextures, 0);
            mFBOTextures = null;
        }
        //删除fbo
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

}
