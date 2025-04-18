// VectorVertex.glsl
#version 330 core


// DRAW CALL SIGNATURES
#define DRAW_LINE               0                   // params: (x2, y2, stroke, cap)
#define DRAW_RECT               1                   // params: (width, height, stroke, _)
#define DRAW_ROUND_RECT         2                   // params: (width, height, stroke, radius)
#define DRAW_ELLIPSE            3                   // params: (width, height, stroke, _)

#define FILL_RECT               4                   // params: (width, height, _, _)
#define FILL_ROUND_RECT         5                   // params: (width, height, radius, _)
#define FILL_ELLIPSE            6                   // params: (width, height, _, _)

#define DRAW_TEXT               7                   // params: (width, height, id, _)

layout (location = 0) in vec2 position;             // (x, y)
layout (location = 1) in vec4 params;               // SEE ABOVE
layout (location = 2) in int type;               // SEE ABOVE
layout (location = 3) in vec4 color;
layout (location = 4) in mat4x4 translate;          // (x, y)
layout (location = 5) in vec4 clip;                 // (topleft.x, topleft.y, width, height)
layout (location = 6) in vec4 viewport;

// Outputs to the geometry shader
out vec2 geoPosition;
out vec4 geoParams;
out int geoType;
out vec4 geoColor;
out vec4 geoClip;
out mat4x4 geoTransform;
out vec4 geoViewport;

void main() {
    // Apply the translation to the position
    gl_Position = vec4(position, 0.0, 1.0);

    // Pass through values to geometry shader
    geoPosition = position;
    geoParams = params;
    geoType = int(type);  // Convert int8_t to int
    geoColor = color;
    geoClip = clip;
    geoTransform = translate;
    geoViewport = viewport;
}