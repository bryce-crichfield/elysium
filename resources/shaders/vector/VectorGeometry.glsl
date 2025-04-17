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

layout (points) in;
layout (triangle_strip, max_vertices = 18) out; // Enough for line with round caps

// Inputs from vertex shader
in vec2 geoPosition[];
in vec4 geoParams[];
in int geoType[];
in vec4 geoColor[];
in vec4 geoClip[];
in mat4x4 geoTransform[];
in vec4 geoViewport[];          // (x, y, width, height)

// Outputs to fragment shader
out vec4 fragViewport;
out vec4 fragColor;
out vec4 fragClip;

const float PI = 3.14159265359;
const int BUTT_CAP = 0;
const int ROUND_CAP = 1;

void EmitScreenVertex(vec2 screenPos) {
    // Convert from screen coordinates to NDC
    vec2 ndcPos;
    ndcPos.x = 2.0 * screenPos.x / geoViewport[0].z - 1.0;
    ndcPos.y = 1.0 - 2.0 * screenPos.y / geoViewport[0].w; // Flip Y

    gl_Position = vec4(ndcPos, 0.0, 1.0);

    // Pass through other attributes...
    fragViewport = geoViewport[0];
    fragColor = geoColor[0];
    fragClip = geoClip[0];

    EmitVertex();
}

void fillRect(vec2 topleft, vec2 size) {
        // Create the 4 corners of the rectangle in screen space
        vec2 topRight = vec2(topleft.x + size.x, topleft.y);
        vec2 bottomLeft = vec2(topleft.x, topleft.y + size.y);
        vec2 bottomRight = vec2(topleft.x + size.x, topleft.y + size.y);

        // Emit vertices in a triangle strip pattern
        // Order: bottom-left, top-left, bottom-right, top-right
        EmitScreenVertex(bottomLeft);
        EmitScreenVertex(topleft);
        EmitScreenVertex(bottomRight);
        EmitScreenVertex(topRight);

        EndPrimitive();
}

void main() {
    if (geoType[0] == FILL_RECT) {
        vec4 start4 = geoTransform[0] * vec4(geoPosition[0], 0.0, 1.0); // (x1, y1)
        vec2 start = start4.xy;
        vec2 size = geoParams[0].xy;                    // (width, height)
        fillRect(start, size);
    }
}