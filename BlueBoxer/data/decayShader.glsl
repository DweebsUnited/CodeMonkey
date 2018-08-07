#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;

varying vec4 vertTexCoord;

void main( ) {
    
    vec3 col = texture2D( texture, vertTexCoord.st ).rgb - vec3( 0.007, 0, 0 );
    
    gl_FragColor = vec4( col, 1.0 );
    
}
