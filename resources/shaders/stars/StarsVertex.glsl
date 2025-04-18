#version 330 core

// Input vertex data
layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;

// Output data for fragment shader
out vec2 fragCoord;

void main() {
    // Output position of the vertex
    gl_Position = vec4(position, 1.0);

    // Pass the texture coordinates to the fragment shader
    fragCoord = texCoord;
}