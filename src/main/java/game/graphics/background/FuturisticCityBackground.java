package game.graphics.background;

import game.Game;
import game.util.Util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FuturisticCityBackground extends Background {
    private final List<Building> buildings = new ArrayList<>();
    private final List<Cloud> clouds = new ArrayList<>();
    private final List<HoverVehicle> vehicles = new ArrayList<>();

    private final Color[] buildingColors = {
            new Color(0xFFFFFF), // Pure white
            new Color(0xF0F5FF), // Slight blue tint
            new Color(0xFFF8E8), // Warm white
            new Color(0xFFFAF0)  // Ivory
    };

    private final Color[] accentColors = {
            new Color(0xFFD700), // Gold
            new Color(0xFFC125), // Golden yellow
            new Color(0xF5DEB3), // Wheat
            new Color(0xDAA520)  // Goldenrod
    };

    private final Color skyTop = new Color(0x87CEEB);     // Sky blue
    private final Color skyBottom = new Color(0xE0F7FF);  // Light sky blue
    private final Color sunColor = new Color(0xFFCC33);   // Bright sun

    private float time = 0;
    private float sunX, sunY;

    public FuturisticCityBackground(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);

        // Initialize sun position
        sunX = screenWidth * 0.75f;
        sunY = screenHeight * 0.2f;

        // Create buildings with different heights and widths
        int buildingCount = 15;
        float buildingWidthMin = screenWidth / 20f;
        float buildingWidthMax = screenWidth / 10f;

        // Sort buildings by distance (for parallax)
        for (int layer = 0; layer < 3; layer++) {
            float layerScale = 0.5f + (layer * 0.25f); // Back to front scaling
            float baseHeight = screenHeight * 0.3f * layerScale;
            float speedFactor = 0.2f + (layer * 0.4f); // Slower in back, faster in front

            for (int i = 0; i < buildingCount / 3; i++) {
                float width = Util.random(buildingWidthMin, buildingWidthMax) * layerScale;
                float height = baseHeight + Util.random(screenHeight * 0.1f, screenHeight * 0.5f) * layerScale;
                float x = Util.random(0, screenWidth);

                Building building = new Building(
                        x,
                        screenHeight - height,
                        width,
                        height,
                        buildingColors[Util.random(0, buildingColors.length - 1)],
                        accentColors[Util.random(0, accentColors.length - 1)],
                        speedFactor
                );
                buildings.add(building);
            }
        }

        // Create clouds
        for (int i = 0; i < 8; i++) {
            Cloud cloud = new Cloud(
                    Util.random(-100, screenWidth),
                    Util.random(50, screenHeight / 3),
                    Util.random(100, 200),
                    Util.random(40, 80),
                    Util.random(5, 15)
            );
            clouds.add(cloud);
        }

        // Create hover vehicles
        for (int i = 0; i < 12; i++) {
            // Different layers/distances
            float layerDepth = Util.random(0.5f, 1.5f);
            float size = 30 / layerDepth;
            float speed = Util.random(50, 200) * layerDepth;

            HoverVehicle vehicle = new HoverVehicle(
                    Util.random(-100, screenWidth + 100),
                    Util.random(screenHeight * 0.3f, screenHeight * 0.7f),
                    size,
                    speed,
                    layerDepth,
                    accentColors[Util.random(0, accentColors.length - 1)]
            );
            vehicles.add(vehicle);
        }
    }

    @Override
    public void update(Duration delta) {
        float dt = Util.perSecond(delta);
        time += dt;

        // Update buildings
        for (Building building : buildings) {
            building.update(dt);
        }

        // Update clouds
        for (Cloud cloud : clouds) {
            cloud.update(dt);
            if (cloud.x > screenWidth + cloud.width) {
                cloud.x = -cloud.width;
                cloud.y = Util.random(50, screenHeight / 3);
            }
        }

        // Update vehicles
        for (HoverVehicle vehicle : vehicles) {
            vehicle.update(dt);

            // Reset vehicles that go off screen
            if (vehicle.direction > 0 && vehicle.x > screenWidth + 100) {
                vehicle.x = -100;
                vehicle.y = Util.random(screenHeight * 0.3f, screenHeight * 0.7f);
            } else if (vehicle.direction < 0 && vehicle.x < -100) {
                vehicle.x = screenWidth + 100;
                vehicle.y = Util.random(screenHeight * 0.3f, screenHeight * 0.7f);
            }
        }
    }

    @Override
    public void render(Graphics2D graphics) {
        // Draw sky gradient
        GradientPaint skyGradient = new GradientPaint(
                0, 0, skyTop,
                0, screenHeight * 0.7f, skyBottom
        );
        graphics.setPaint(skyGradient);
        graphics.fillRect(0, 0, screenWidth, screenHeight);

        // Draw sun with glow
        drawSun(graphics, sunX, sunY, 60);

        // Draw clouds
        for (Cloud cloud : clouds) {
            cloud.render(graphics);
        }

        // Draw buildings (sorted by layer)
        buildings.sort((b1, b2) -> Float.compare(b1.speedFactor, b2.speedFactor));
        for (Building building : buildings) {
            building.render(graphics);
        }

        // Draw vehicles
        for (HoverVehicle vehicle : vehicles) {
            vehicle.render(graphics);
        }
    }

    private void drawSun(Graphics2D graphics, float x, float y, float radius) {
        // Draw outer glow
        RadialGradientPaint sunGlow = new RadialGradientPaint(
                new Point2D.Float(x, y),
                radius * 2,
                new float[] {0.0f, 0.7f, 1.0f},
                new Color[] {
                        new Color(sunColor.getRed(), sunColor.getGreen(), sunColor.getBlue(), 150),
                        new Color(sunColor.getRed(), sunColor.getGreen(), sunColor.getBlue(), 70),
                        new Color(sunColor.getRed(), sunColor.getGreen(), sunColor.getBlue(), 0)
                }
        );

        Paint oldPaint = graphics.getPaint();
        graphics.setPaint(sunGlow);
        graphics.fillOval((int)(x - radius * 2), (int)(y - radius * 2), (int)(radius * 4), (int)(radius * 4));

        // Draw sun itself
        graphics.setPaint(new GradientPaint(
                x - radius/2, y - radius/2, Color.WHITE,
                x + radius/2, y + radius/2, sunColor
        ));
        graphics.fillOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));

        graphics.setPaint(oldPaint);
    }

    // Inner class for buildings
    private class Building {
        private float x, y, width, height;
        private Color color, accentColor;
        private float speedFactor;
        private float glowIntensity = 0;
        private float[] windowRows;

        public Building(float x, float y, float width, float height, Color color, Color accentColor, float speedFactor) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.accentColor = accentColor;
            this.speedFactor = speedFactor;

            // Create window pattern
            int numRows = (int)(height / 20);
            windowRows = new float[numRows];
            for (int i = 0; i < numRows; i++) {
                windowRows[i] = Util.random(0f, 1f);
            }
        }

        public void update(float dt) {
            // Apply parallax effect
            x -= 20 * speedFactor * dt;
            if (x < -width) {
                x = screenWidth;
            }

            // Animate building glow
            glowIntensity = (float)(0.5f + 0.5f * Math.sin(time * 0.5f));
        }

        public void render(Graphics2D graphics) {
            // Save the original transform
            AffineTransform originalTransform = graphics.getTransform();

            // Main building shape
            Path2D buildingShape = new Path2D.Float();

            // Create curved top
            float curveHeight = height * 0.1f;
            float controlPointOffset = width * 0.3f;

            buildingShape.moveTo(x, y + height);
            buildingShape.lineTo(x, y + curveHeight);

            // Curved top using cubic curve
            buildingShape.curveTo(
                    x + controlPointOffset, y,
                    x + width - controlPointOffset, y,
                    x + width, y + curveHeight
            );

            buildingShape.lineTo(x + width, y + height);
            buildingShape.closePath();

            // Create gradient for building
            GradientPaint buildingGradient = new GradientPaint(
                    x, y, color,
                    x + width, y, new Color(
                    Math.min(255, color.getRed() + 20),
                    Math.min(255, color.getGreen() + 20),
                    Math.min(255, color.getBlue() + 20)
            ));

            graphics.setPaint(buildingGradient);
            graphics.fill(buildingShape);

            // Draw accent lines
            graphics.setColor(accentColor);
            float accentWidth = 3f;

            // Horizontal accent lines
            for (int i = 1; i < 4; i++) {
                float lineY = y + (height * i / 4);
                graphics.setStroke(new BasicStroke(accentWidth));
                graphics.drawLine((int)x, (int)lineY, (int)(x + width), (int)lineY);
            }

            // Draw windows
            float windowWidth = width * 0.15f;
            float windowHeight = height * 0.03f;
            float windowSpacing = width * 0.25f;

            for (int row = 0; row < windowRows.length; row++) {
                float rowY = y + (height * 0.1f) + (row * windowHeight * 1.5f);
                float rowOffset = windowRows[row] * windowWidth;

                for (float wx = x + rowOffset; wx < x + width - windowWidth; wx += windowSpacing) {
                    // Window glow effect
                    if (Math.random() < 0.7f) {
                        float thisWindowGlow = (float)Math.pow(glowIntensity + Math.random() * 0.3f, 2);

                        // Window light
                        float blue = 200 + (int)(55 * thisWindowGlow);
                        // clamp blue to 255
                        blue = Math.min(255, blue);
                        Color windowColor = new Color(
                                255,
                                255,
                                (int)blue
                        );

                        graphics.setColor(windowColor);
                        graphics.fillRoundRect(
                                (int)wx,
                                (int)rowY,
                                (int)windowWidth,
                                (int)windowHeight,
                                5, 5
                        );
                    }
                }
            }

            // Restore original transform
            graphics.setTransform(originalTransform);
        }
    }

    // Inner class for clouds
    private class Cloud {
        float x, y, width, height, speed;

        public Cloud(float x, float y, float width, float height, float speed) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = speed;
        }

        public void update(float dt) {
            x += speed * dt;
        }

        public void render(Graphics2D graphics) {
            graphics.setColor(new Color(255, 255, 255, 180));

            // Draw cloud as a series of overlapping circles
            int segments = 5;
            float segmentWidth = width / segments;

            for (int i = 0; i < segments; i++) {
                float blobSize = height * (0.7f + 0.6f * (float)Math.sin(i * 1.3f));
                float yOffset = height * 0.1f * (float)Math.sin(i * 2.5f + time * 0.5f);

                graphics.fillOval(
                        (int)(x + i * segmentWidth * 0.8f),
                        (int)(y + yOffset),
                        (int)blobSize,
                        (int)blobSize
                );
            }
        }
    }

    // Inner class for hover vehicles
    private class HoverVehicle {
        float x, y, size, speed, depth;
        float direction;
        Color color;
        float animationOffset;

        public HoverVehicle(float x, float y, float size, float speed, float depth, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.depth = depth;
            this.color = color;
            this.direction = Math.random() > 0.5 ? 1 : -1;
            this.animationOffset = Util.random(0, 10);
        }

        public void update(float dt) {
            x += speed * direction * dt;

            // Slight vertical bobbing
            y += Math.sin(time * 2 + animationOffset) * 0.5f;
        }

        public void render(Graphics2D graphics) {
            // Save original transform
            AffineTransform originalTransform = graphics.getTransform();

            // Apply transform
            graphics.translate(x, y);
            if (direction < 0) {
                graphics.scale(-1, 1); // Flip if going left
            }

            // Vehicle body - curved shape
            Path2D vehicleBody = new Path2D.Float();
            float length = size * 3;
            float height = size;

            vehicleBody.moveTo(0, 0);
            vehicleBody.curveTo(
                    length * 0.3f, -height * 0.5f,
                    length * 0.7f, -height * 0.5f,
                    length, 0
            );
            vehicleBody.curveTo(
                    length * 0.7f, height * 0.5f,
                    length * 0.3f, height * 0.5f,
                    0, 0
            );

            // Draw light trail
            float trailLength = length * 2;
            Composite originalComposite = graphics.getComposite();
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

            GradientPaint trailGradient = new GradientPaint(
                    0, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 150),
                    -trailLength, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
            );
            graphics.setPaint(trailGradient);
            graphics.fillRect((int)-trailLength, (int)-height/4, (int)trailLength, (int)height/2);

            graphics.setComposite(originalComposite);

            // Draw vehicle body
            GradientPaint bodyGradient = new GradientPaint(
                    0, -height/2, Color.WHITE,
                    0, height/2, color
            );
            graphics.setPaint(bodyGradient);
            graphics.fill(vehicleBody);

            // Draw cockpit/window
            graphics.setColor(new Color(135, 206, 250, 200)); // Light blue window
            graphics.fillOval((int)(length * 0.4f), (int)(-height * 0.25f), (int)(length * 0.2f), (int)(height * 0.5f));

            // Restore original transform
            graphics.setTransform(originalTransform);
        }
    }
}