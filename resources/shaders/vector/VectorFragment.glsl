#version 330 core

in vec4 fragViewport;          // (x, y, width, height)
in vec4 fragColor;
in vec4 fragClip;

out vec4 FragColor;

void main() {
    // Convert gl_FragCoord to the same coordinate system as fragClip
    // gl_FragCoord has origin at bottom-left, fragClip has origin at top-left
    vec2 screenPos = vec2(gl_FragCoord.x, fragViewport.w - gl_FragCoord.y);

    // Check if in clip region
    if (screenPos.x < fragClip.x || screenPos.x > fragClip.x + fragClip.z ||
    screenPos.y < fragClip.y || screenPos.y > fragClip.y + fragClip.w) {
        discard;
    }

    FragColor = fragColor;
}