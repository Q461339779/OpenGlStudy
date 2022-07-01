//片源着色器
//设置工作精度  highp,mediump,lowp
precision mediump float;
//四分量数据RGBA
uniform vec4 v_Color;

void main(){

    gl_FragColor = v_Color;
}