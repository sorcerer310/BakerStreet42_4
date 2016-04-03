#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 vColor;
varying vec2 vTexCoord;
uniform sampler2D u_texture;
void main() {
	vec4 texColor = texture2D(u_texture, vTexCoord);
	texColor.rgb = vec3(0.2126*texColor.r+0.7152*texColor.g+0.0722*texColor.b);
	gl_FragColor = texColor * vColor;
}