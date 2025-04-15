// SpriteFragment.glsl
#version 330 core

in vec2 fragTexCoord;
in vec4 fragColor;
in float fragTexIndex;

out vec4 finalColor;

uniform sampler2D textures[16]; // Array of texture samplers

void main() {
    int index = int(fragTexIndex);
    vec4 texColor = texture(textures[index], fragTexCoord);
    finalColor = texColor * fragColor;
}