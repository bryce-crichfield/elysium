package sampleGame.battle.util;

import client.runtime.application.Application;
import client.core.graphics.Transform;

public class Camera {
    Application game;
    private float x;
    private float y;
    private float zoom;

    public Camera(Application game) {
        this.game = game;
        x = 0;
        y = 0;
        zoom = 1;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public Transform getTransform() {
        // Center the camera on the screen
        float screenWidth = Application.SCREEN_WIDTH / zoom;
        float screenHeight = Application.SCREEN_HEIGHT / zoom;
        int cameraX = (int) (x - (screenWidth / 2));
        int cameraY = (int) (y - (screenHeight / 2));

        return new Transform()
                .scale(zoom, zoom)
                .translate(-cameraX, -cameraY);
    }

    public int getWorldX(int screenX) {
        // Convert screen coordinates to world coordinates
        float screenCenterX = Application.SCREEN_WIDTH / 2.0f;
        float worldX = (screenX - screenCenterX) / zoom + x;
        return Math.round(worldX);
    }

    public int getWorldY(int screenY) {
        // Convert screen coordinates to world coordinates
        float screenCenterY = Application.SCREEN_HEIGHT / 2.0f;
        float worldY = (screenY - screenCenterY) / zoom + y;
        return Math.round(worldY);
    }
}
