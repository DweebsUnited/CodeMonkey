#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D base;
uniform sampler2D mask;
uniform sampler2D frost;

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertTexCoord;

varying vec4 vertColor;

void main( ) {
    
    vec2 txc = vec2( vertTexCoord.s, 1.0 - vertTexCoord.t );
    
    vec3 baseCol = texture2D( base, txc ).rgb;
    // Mask needs original coords for some reason
    vec3 maskCol = texture2D( mask, vertTexCoord.st ).rgb;
    vec3 frostCol = texture2D( frost, txc ).rgb;
    
    vec3 rawCol = texture2D( texture, txc ).rgb;
    
    // TODO: Emboss, give it some depth if on the border of the mask
    
    vec3 col = mix( frostCol, baseCol, maskCol.r );
    
    gl_FragColor = vec4( col, 1.0 );
    
}
