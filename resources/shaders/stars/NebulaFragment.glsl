#version 330 core

in vec2 fragCoord;
out vec4 fragColor;

uniform vec2 resolution;
uniform float time;

//------------------------------------------------------------------------------
// TUNABLE PARAMETERS
//------------------------------------------------------------------------------
#define PLASMA_SCALE 3.0      // Scale of the plasma
#define FLOW_SPEED 0.3        // Speed of plasma flow
#define COLOR_INTENSITY 1.2   // Intensity of colors
#define PLASMA_DETAIL 2.0     // Level of detail in plasma patterns
#define WARP_AMOUNT 0.2       // Amount of warping in the plasma
#define ELECTRIC_EFFECT 0.7   // Strength of electric patterns
#define PULSE_SPEED 2.0       // Speed of pulsing

// Color scheme
#define COLOR1 vec3(0.1, 0.3, 0.9)  // Blue
#define COLOR2 vec3(0.1, 0.0, 0.8)  // Purple
#define COLOR3 vec3(0.0, 0.2, 0.4)  // Teal
#define ELECTRIC_COLOR vec3(0.1, 0.1, 0.1)  // White/blue for electric accents

// Sine wave
float sinwave(float x, float phase, float freq) {
    return sin(x * freq + phase);
}

// Cosine wave
float coswave(float x, float phase, float freq) {
    return cos(x * freq + phase);
}

void main() {
    // Normalized coordinates
    vec2 uv = fragCoord;
    float t = time * FLOW_SPEED;
    
    // Warping the coordinates
    uv.x += WARP_AMOUNT * sin(uv.y * 10.0 + t);
    uv.y += WARP_AMOUNT * cos(uv.x * 10.0 - t * 0.5);
    
    // Create plasma effect
    float plasma = 0.0;
    
    // Layer 1
    plasma += sinwave(uv.x * PLASMA_SCALE, t * 1.1, 5.0) * 0.5 + 0.5;
    
    // Layer 2
    plasma += sinwave(uv.y * PLASMA_SCALE, t * 0.5, 3.0) * 0.5 + 0.5;
    
    // Layer 3
    plasma += sinwave(distance(uv, vec2(0.5 + 0.1 * sin(t), 0.5 + 0.1 * cos(t))) * PLASMA_SCALE * 2.0, t, 1.0) * 0.5 + 0.5;
    
    // Layer 4
    plasma += sinwave(distance(uv, vec2(0.7 * cos(t * 0.33), 0.7 * sin(t * 0.33))) * PLASMA_SCALE, t * 1.3, 2.0) * 0.5 + 0.5;
    
    // Normalize
    plasma *= 0.25 * PLASMA_DETAIL;
    
    // Create color from plasma value
    vec3 color;
    float pulse = 0.5 + 0.5 * sin(time * PULSE_SPEED);
    
    // Color mixing based on plasma value
    if (plasma < 0.33) {
        color = mix(COLOR1, COLOR2, plasma * 3.0);
    } else if (plasma < 0.66) {
        color = mix(COLOR2, COLOR3, (plasma - 0.33) * 3.0);
    } else {
        color = mix(COLOR3, COLOR1, (plasma - 0.66) * 3.0);
    }
    
    // Add electric effect
    if (fract(plasma * 10.0 + t) < 0.02 * ELECTRIC_EFFECT) {
        color = mix(color, ELECTRIC_COLOR, 0.8 * pulse);
    }
    
    // Add pulsing glow
    color *= 0.8 + 0.4 * pulse;
    
    // Apply intensity
    color *= COLOR_INTENSITY;
    
    fragColor = vec4(color, 1.0);
}