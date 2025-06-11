package client.core.graphics.sprite;

import client.core.graphics.texture.Texture;
import lombok.Getter;


public class Sprite {
    @Getter
    private final Texture texture;
    private final float subX ;
    private final float subY;
    private final float subWidth;
    private final float subHeight;

    public Sprite(Texture texture, float subX, float subY, float subWidth, float subHeight) {
        this.texture = texture;
        this.subX = subX;
        this.subY = subY;
        this.subWidth = subWidth;
        this.subHeight = subHeight;
    }

    public Sprite(Texture texture) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    float[] getSubImageCoordinates() {
        float u1 = subX / texture.getWidth();
        float v1 = subY / texture.getHeight();
        float u2 = (subX + subWidth) / texture.getWidth();
        float v2 = (subY + subHeight) / texture.getHeight();

        // Return texture coordinates in the order expected by drawSprite:
        // Bottom-left, Bottom-right, Top-right, Top-left
        return new float[] {
                u1, v1,  // Bottom-left
                u2, v1,  // Bottom-right
                u2, v2,  // Top-right
                u1, v2   // Top-left
        };
    }
}