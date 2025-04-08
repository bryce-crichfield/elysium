package game.graphics.background;

import game.Game;
import game.util.Util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LavaLampNebulaBackground extends Background {
    private final List<GasCloud> gasClouds = new ArrayList<>();
    private final Color[] nebulaPalette = {
            new Color(0x9b59b6), // Purple
            new Color(0x3498db), // Blue
            new Color(0x1abc9c), // Teal
            new Color(0xe74c3c)  // Red
    };

    private float time = 0;

    public LavaLampNebulaBackground(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);

        // Create several overlapping gas clouds
        for (int i = 0; i < 5; i++) {
            Color baseColor = nebulaPalette[Util.random(0, nebulaPalette.length - 1)];
            float alpha = Util.random(0.1f, 0.3f);
            Color cloudColor = new Color(
                    baseColor.getRed()/255f,
                    baseColor.getGreen()/255f,
                    baseColor.getBlue()/255f,
                    alpha
            );

            int cloudSize = Util.random(screenWidth / 2, screenWidth);

            // Add control points for Bezier movement
            int numControlPoints = 4;
            float[][] controlPoints = new float[numControlPoints][2];
            for (int j = 0; j < numControlPoints; j++) {
                controlPoints[j][0] = Util.random(screenWidth * 0.2f, screenWidth * 0.8f);
                controlPoints[j][1] = Util.random(screenHeight * 0.2f, screenHeight * 0.8f);
            }

            gasClouds.add(new GasCloud(
                    Util.random(0, screenWidth),
                    Util.random(0, screenHeight),
                    cloudSize,
                    cloudColor,
                    controlPoints,
                    Util.random(5f, 15f) // Movement speed
            ));
        }
    }

    @Override
    public void update(Duration delta) {
        float dt = Util.perSecond(delta);
        time += dt;

        for (GasCloud cloud : gasClouds) {
            cloud.update(dt, time);
        }
    }

    @Override
    public void render(Graphics2D graphics) {
        // Create a dark space background with stars
        // Deep space blue-black gradient
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0x000011),
                0, screenHeight, new Color(0x000033)
        );
        graphics.setPaint(gradient);
        graphics.fillRect(0, 0, screenWidth, screenHeight);

        // Add some distant stars
        drawDistantStars(graphics);

        // Render gas clouds with soft edges
        for (GasCloud cloud : gasClouds) {
            cloud.render(graphics);
        }
    }

    private void drawDistantStars(Graphics2D graphics) {
        // Draw a few dim stars in the background
        graphics.setColor(new Color(1f, 1f, 1f, 0.7f));
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.sin(i * 7.3) * screenWidth + screenWidth / 2) % screenWidth;
            int y = (int) (Math.cos(i * 3.7) * screenHeight + screenHeight / 2) % screenHeight;
            int size = Util.random(1, 2);
            graphics.fillOval(x, y, size, size);
        }
    }

    // Inner class to represent a nebula gas cloud
    private class GasCloud {
        float x, y;
        int size;
        Color color;
        float[][] controlPoints;
        float pathProgress = 0;
        float movementSpeed;
        float noiseOffset;

        // For shape morphing
        float morphProgress = 0;
        float morphRate = Util.random(0.2f, 0.5f);
        float xScale = 1.0f;
        float yScale = 1.0f;
        float rotation = 0;

        public GasCloud(float x, float y, int size, Color color, float[][] controlPoints, float movementSpeed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.controlPoints = controlPoints;
            this.movementSpeed = movementSpeed;
            this.noiseOffset = Util.random(0, 1000);
        }

        public void update(float dt, float time) {
            // Update path progress - loop around path
            pathProgress += dt * (movementSpeed / 100f);
            if (pathProgress > 1) {
                pathProgress -= 1;

                // Randomize control points slightly on each loop
                for (int i = 0; i < controlPoints.length; i++) {
                    controlPoints[i][0] += Util.random(-20, 20);
                    controlPoints[i][1] += Util.random(-20, 20);

                    // Keep within screen bounds
                    controlPoints[i][0] = Math.max(0, Math.min(screenWidth, controlPoints[i][0]));
                    controlPoints[i][1] = Math.max(0, Math.min(screenHeight, controlPoints[i][1]));
                }
            }

            // Use Bezier curve to determine position
            float t = pathProgress;
            float invT = 1 - t;

            // For simplicity, using a cubic Bezier with 4 control points
            float a = invT * invT * invT;
            float b = 3 * t * invT * invT;
            float c = 3 * t * t * invT;
            float d = t * t * t;

            x = a * controlPoints[0][0] + b * controlPoints[1][0] + c * controlPoints[2][0] + d * controlPoints[3][0];
            y = a * controlPoints[0][1] + b * controlPoints[1][1] + c * controlPoints[2][1] + d * controlPoints[3][1];

            // Update morphing
            morphProgress += dt * morphRate;
            if (morphProgress > Math.PI * 2) {
                morphProgress -= Math.PI * 2;
            }

            // Calculate stretching and rotation based on morph progress
            xScale = 1.0f + 0.3f * (float)Math.sin(morphProgress);
            yScale = 1.0f + 0.3f * (float)Math.cos(morphProgress * 0.7f);
            rotation = 0.1f * (float)Math.sin(morphProgress * 0.5f);

            // Slowly change noise offset for gradient center movement
            noiseOffset += dt * 0.1f;
        }

        public void render(Graphics2D graphics) {
            // Save the original transform
            AffineTransform originalTransform = graphics.getTransform();

            // Apply rotation and scaling centered on the cloud
            graphics.translate(x + size/2, y + size/2);
            graphics.rotate(rotation);
            graphics.scale(xScale, yScale);
            graphics.translate(-(x + size/2), -(y + size/2));

            // Create a radial gradient with shifting center
            Paint originalPaint = graphics.getPaint();

            // Shift gradient center based on noise
            float centerShiftX = size * 0.2f * (float)Math.sin(noiseOffset * 1.3f);
            float centerShiftY = size * 0.2f * (float)Math.cos(noiseOffset * 0.7f);

            RadialGradientPaint cloudGradient = new RadialGradientPaint(
                    new Point2D.Float(x + size/2 + centerShiftX, y + size/2 + centerShiftY),
                    size/2,
                    new float[] {0.0f, 0.7f, 1.0f},
                    new Color[] {
                            color,
                            new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f * 0.5f),
                            new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0f)
                    }
            );

            graphics.setPaint(cloudGradient);
            graphics.fillOval((int)x, (int)y, size, size);

            // Restore original paint and transform
            graphics.setPaint(originalPaint);
            graphics.setTransform(originalTransform);
        }
    }
}