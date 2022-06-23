//片源着色器
//设置工作精度  highp,mediump,lowp
precision mediump float;
//四分量数据RGBA
uniform vec4 u_Color;

void main(){

    gl_FragColor = u_Color;
}