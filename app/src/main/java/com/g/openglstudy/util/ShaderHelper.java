package com.g.openglstudy.util;

import android.opengl.GLES20;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glValidateProgram;

/**
 * 着色器帮助类
 */
public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    /**
     * 编译顶点着色器代码
     *
     * @param shaderCode 顶点着色器代码
     * @return 着色器代码对象
     */
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 编译片源着色器代码
     *
     * @param shaderCode 片源着色器代码
     * @return 着色器代码对象
     */
    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 编译着色器方法
     *
     * @param type       着色器类型
     * @param shaderCode 着色器代码字符串
     * @return 着色器代码对象 id
     */
    private static int compileShader(int type, String shaderCode) {
        //创建新的着色器
        int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            //提示创建着色器失败
            return 0;
        }
        //将 shaderCode 读取到 OpenGL 着色器 与shaderObjectId所引用的着色器对象关联在一起
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        //编译着色器
        GLES20.glCompileShader(shaderObjectId);
        //检查着色器是否编译成功 0是编译失败 1是成功
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        //编译失败 着色器没用 删除着色器
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;

    }


    //

    /**
     * 着色器程序把顶点着色器和片源着色器链接到一起
     *
     * @param vertexShaderId   顶点着色器id
     * @param fragmentShaderId 片源着色器id
     * @return 着色器程序id
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        //创建着色器程序
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            return 0;
        }
        //添加顶点着色器到着色器程序
        glAttachShader(programObjectId, vertexShaderId);
        //添加片源着色器到着色器程序
        glAttachShader(programObjectId, fragmentShaderId);
        //将顶点着色器和片源着色器链接到一起
        glLinkProgram(programObjectId);
        //验证链接状态
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            return 0;
        }
        return programObjectId;
    }

    public static int buildProgram(String vertexShaderSource,String fragmentShaderSource){

        int programObjectId;

        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        programObjectId = linkProgram(vertexShader,fragmentShader);

        validateProgram(programObjectId);

        return programObjectId;
    }

    private static boolean validateProgram(int programObjectId) {
        //校验程序
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId,GL_VALIDATE_STATUS,validateStatus,0);

        return validateStatus[0] == GLES20.GL_FALSE;
    }


}
