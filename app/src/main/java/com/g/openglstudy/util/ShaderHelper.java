package com.g.openglstudy.util;

import android.opengl.GLES20;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glCreateShader;

/**
 * 着色器帮助类
 */
public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    /**
     * 编译顶点着色器代码
     * @param shaderCode 顶点着色器代码
     * @return 着色器代码对象
     */
    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 编译片源着色器代码
     * @param shaderCode 片源着色器代码
     * @return 着色器代码对象
     */
    public static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 编译着色器方法
     * @param type  着色器类型
     * @param shaderCode 着色器代码字符串
     * @return 着色器代码对象
     */
    private static int compileShader(int type, String shaderCode) {
        //创建新的着色器
        int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0){
            //提示创建着色器失败
            return 0;
        }
        //将 shaderCode 读取到 OpenGL 着色器 与shaderObjectId所引用的着色器对象关联在一起
        GLES20.glShaderSource(shaderObjectId,shaderCode);
        //编译着色器
        GLES20.glCompileShader(shaderObjectId);

        return 0;
    }
}
