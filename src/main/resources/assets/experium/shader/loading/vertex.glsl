#version 120

attribute vec2 uv;
varying vec2 vectorUV;

void main(void) {
    //Map gl_MultiTexCoord0 to index zero of gl_TexCoord
    gl_TexCoord[0] = gl_MultiTexCoord0;

    //Calculate position by multiplying model, view and projection matrix by the vertex vector
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    vectorUV = uv;
}