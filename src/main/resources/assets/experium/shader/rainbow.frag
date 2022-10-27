#extension GL_OES_standard_derivatives : enable

precision mediump float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform sampler2D texture;

void main( void ) {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);
	gl_FragColor = vec4(sin(time), cos(mouse.y), sin(cos(mouse.x)), centerCol.a);
}