#version 330 core

// Input from vertex shader
in vec2 fragCoord;

// Output
out vec4 fragColor;

// Uniforms
uniform vec2 resolution;   // Screen resolution
uniform float time;        // Time in seconds

//------------------------------------------------------------------------------
// TUNABLE PARAMETERS
//------------------------------------------------------------------------------
// Star count and appearance
#define NUM_STARS 250              // Number of stars
#define MIN_SIZE 2.0               // Minimum star size
#define MAX_SIZE 8.0               // Maximum star size
#define BRIGHTNESS_MIN 0.5         // Minimum star brightness
#define BRIGHTNESS_MAX 1.0         // Maximum star brightness
#define GLOW_INTENSITY 0.1         // Star glow intensity (0.0-1.0)
#define CORE_SIZE_RATIO 0.1      // Ratio of core size to total size
#define STAR_INTENSITY 2         // Overall star intensity

// Speed parameters
#define SPEED_FACTOR 1.5           // Overall speed multiplier
#define MAX_SPEED 50.0            // Maximum star speed
#define MIN_SPEED 25.0             // Minimum star speed

// Warble effect parameters
#define WARBLE_AMOUNT 2          // Amount of size variation from warble
#define WARBLE_SPEED_MIN 0.5      // Minimum warble speed
#define WARBLE_SPEED_MAX 1       // Maximum warble speed
#define WARBLE_RATE_MIN 10.0       // Minimum warble rate
#define WARBLE_RATE_MAX 50.0       // Maximum warble rate

// Background colors
#define BG_COLOR_TOP vec3(0.0, 0.027, 0.267)  // Top gradient color (dark blue)
#define BG_COLOR_BOTTOM vec3(0.0, 0.0, 0.0)   // Bottom gradient color (black)
#define INVERT_GRADIENT false                 // Set to true to invert gradient direction

//------------------------------------------------------------------------------
// Hash function for randomness
float hash(float n) {
    return fract(sin(n) * 43758.5453123);
}

// Random functions
float random(float min, float max, float seed) {
    return min + (max - min) * hash(seed);
}

// Map function similar to the Java Util.map
float map(float value, float inMin, float inMax, float outMin, float outMax) {
    return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

// Star shape function
float starShape(float dist, float size) {
    // Hard center with soft glow
    float core = smoothstep(size * CORE_SIZE_RATIO, 0.0, dist);
    float glow = max(0.0, 1.0 - dist / size);
    return core + glow * GLOW_INTENSITY;
}

void main() {
    // Background gradient from bottom to top color
    float gradientPosition = INVERT_GRADIENT ? (1.0 - fragCoord.y) : fragCoord.y;
    vec3 color = mix(BG_COLOR_BOTTOM, BG_COLOR_TOP, gradientPosition);

    // Simulate the array of stars
    for (int i = 0; i < NUM_STARS; i++) {
        float seed = float(i) * 1234.5678;

        // Generate star properties
        float starX = random(0.0, resolution.x, seed + 1.0);
        float starY = random(0.0, resolution.y, seed + 2.0);

        // Size with power distribution
        float size = mix(MIN_SIZE, MAX_SIZE, pow(hash(seed + 3.0), 1.5));

        // Speed inversely proportional to size (big stars are slow, small stars are fast)
        float speed = map(size, MIN_SIZE, MAX_SIZE, MAX_SPEED, MIN_SPEED) * SPEED_FACTOR;

        // Update star position based on time
        starY = mod(starY - speed * time, resolution.y);

        // Warble effect
        float warbleSpeed = random(WARBLE_SPEED_MIN, WARBLE_SPEED_MAX, seed + 5.0);
        float warble = random(WARBLE_RATE_MIN, WARBLE_RATE_MAX, seed + 4.0) + time * warbleSpeed;
        float sizeMod = sin(warble * 6.28318) * WARBLE_AMOUNT;
        float finalSize = size + sizeMod;

        // Color based on size (brightness) - big stars are bright/white, small stars are dimmer
        float brightness = map(size, MIN_SIZE, MAX_SIZE, BRIGHTNESS_MIN, BRIGHTNESS_MAX);
        vec3 starColor = vec3(brightness);

        // Calculate star shape
        vec2 fragPos = gl_FragCoord.xy;  // Use gl_FragCoord for pixel coordinates
        float dist = length(fragPos - vec2(starX, starY));
        float star = starShape(dist, finalSize);

        // Add star to scene with additive blending
        color += starColor * star * STAR_INTENSITY;
    }

    // Clamp final color to prevent over-saturation
    color = min(color, vec3(1.0));

    fragColor = vec4(color, 1.0);
}