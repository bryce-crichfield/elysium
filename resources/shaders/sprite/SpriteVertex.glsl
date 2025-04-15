// SpriteVertex.glsl
#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec4 color;
layout (location = 3) in float texIndex;

out vec2 fragTexCoord;
out vec4 fragColor;
out float fragTexIndex;

uniform mat4 projection;
uniform mat4 view;

void main() {
    fragTexCoord = texCoord;
    fragColor = color;
    fragTexIndex = texIndex;
    gl_Position = projection * view * vec4(position, 1.0);
}