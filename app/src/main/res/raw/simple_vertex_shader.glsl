//顶点着色器

// vec4 4分量浮点向量 xyzw
// 若w==1，则向量（x, y, z, 1）为空间中的点。
// 若w==0，则向量（x, y, z, 0）为方向向量。

// attribute 顶点数据传入到着色器 用于经常更改的信息，只能在顶点着色器中使用；
attribute vec4 a_Position;
//着色器入口
void main(){
    // 将传进来的 a_Position 赋值到 gl_Position
    gl_Position = a_Position;
}