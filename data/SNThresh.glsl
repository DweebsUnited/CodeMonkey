#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform vec3 cCol;
uniform float t;

uniform sampler2D texture;

varying vec4 vertTexCoord;

void main( ) {
    
    vec3 col = texture2D( texture, vertTexCoord.st ).rgb;
    
    if( abs( col.r - cCol.r ) < t && abs( col.g - cCol.g ) < t && abs( col.b - cCol.b ) < t ) {
        gl_FragColor = vec4( 1.0, 1.0, 1.0, 1.0 );
    } else {
        gl_FragColor = vec4( 0.0, 0.0, 0.0, 1.0 );
    }
    
}
