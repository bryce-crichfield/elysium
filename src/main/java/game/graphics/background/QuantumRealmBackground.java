package game.graphics.background;

import game.Game;
import game.util.Util;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class QuantumRealmBackground extends Background {
    private final List<EnergyField> energyFields = new ArrayList<>();
    private final List<QuantumParticle> particles = new ArrayList<>();
    private final List<WavePattern> wavePatterns = new ArrayList<>();

    private float time = 0;

    // Color scheme - vibrant blue, purple, teal with some warm accents
    private final Color[] fieldColors = {
            new Color(0x3498db),  // Blue
            new Color(0x9b59b6),  // Purple
            new Color(0x1abc9c),  // Teal
            new Color(0x8e44ad)   // Dark purple
    };

    private final Color[] particleColors = {
            new Color(0xf39c12),  // Orange
            new Color(0xe74c3c),  // Red
            new Color(0xf1c40f),  // Yellow
            new Color(0x2ecc71)   // Green
    };

    private final Color backgroundColor = new Color(0x0c0c1a); // Very dark blue-purple

    public QuantumRealmBackground(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);

        // Create energy fields - larger flowing patterns
        for (int i = 0; i < 5; i++) {
            float size = Util.random(screenWidth * 0.3f, screenWidth * 0.8f);

            EnergyField field = new EnergyField(
                    Util.random(0, screenWidth),
                    Util.random(0, screenHeight),
                    size,
                    fieldColors[Util.random(0, fieldColors.length - 1)],
                    Util.random(0.1f, 0.5f)
            );
            energyFields.add(field);
        }

        // Create wave patterns
        for (int i = 0; i < 8; i++) {
            float amplitude = Util.random(10, 50);
            float wavelength = Util.random(100, 300);
            float speed = Util.random(0.5f, 2.0f);

            WavePattern wave = new WavePattern(
                    Util.random(0, screenWidth),
                    Util.random(0, screenHeight),
                    amplitude,
                    wavelength,
                    speed,
                    fieldColors[Util.random(0, fieldColors.length - 1)]
            );
            wavePatterns.add(wave);
        }

        // Create quantum particles
        for (int i = 0; i < 80; i++) {
            float size = Util.random(2, 10);
            float speed = Util.random(20, 100);

            QuantumParticle particle = new QuantumParticle(
                    Util.random(0, screenWidth),
                    Util.random(0, screenHeight),
                    size,
                    particleColors[Util.random(0, particleColors.length - 1)],
                    speed
            );
            particles.add(particle);
        }
    }

    @Override
    public void update(Duration delta) {
        float dt = Util.perSecond(delta);
        time += dt;

        // Update energy fields
        for (EnergyField field : energyFields) {
            field.update(dt, time);
        }

        // Update wave patterns
        for (WavePattern wave : wavePatterns) {
            wave.update(dt, time);
        }

        // Update quantum particles
        for (QuantumParticle particle : particles) {
            particle.update(dt, time);

            // Wrap particles around screen edges
            if (particle.x < -particle.size) particle.x = screenWidth + particle.size;
            if (particle.x > screenWidth + particle.size) particle.x = -particle.size;
            if (particle.y < -particle.size) particle.y = screenHeight + particle.size;
            if (particle.y > screenHeight + particle.size) particle.y = -particle.size;
        }
    }

    @Override
    public void render(Graphics2D graphics) {
        // Fill background with dark color
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, screenWidth, screenHeight);

        // Draw grid pattern
        drawQuantumGrid(graphics);

        // Draw wave patterns
        for (WavePattern wave : wavePatterns) {
            wave.render(graphics);
        }

        // Draw energy fields
        for (EnergyField field : energyFields) {
            field.render(graphics);
        }

        // Draw quantum particles
        for (QuantumParticle particle : particles) {
            particle.render(graphics);
        }

        // Add subtle vignette effect
        drawVignette(graphics);
    }

    private void drawQuantumGrid(Graphics2D graphics) {
        // Draw faint grid lines
        graphics.setColor(new Color(255, 255, 255, 30));

        // Grid spacing varies with time for a subtle wave effect
        float spacing = 40 + (float)Math.sin(time * 0.2) * 10;
        float offset = time * 10 % spacing;

        // Horizontal grid lines
        for (float y = -offset; y <= screenHeight; y += spacing) {
            graphics.setStroke(new BasicStroke(1));
            graphics.drawLine(0, (int)y, screenWidth, (int)y);
        }

        // Vertical grid lines
        for (float x = -offset; x <= screenWidth; x += spacing) {
            graphics.drawLine((int)x, 0, (int)x, screenHeight);
        }

        // Highlight a few grid lines for energy effect
        graphics.setColor(new Color(255, 255, 255, 50));
        graphics.setStroke(new BasicStroke(2));

        int highlightSpacing = 120;
        for (float x = (-time * 50) % highlightSpacing; x <= screenWidth; x += highlightSpacing) {
            graphics.drawLine((int)x, 0, (int)x, screenHeight);
        }
    }

    private void drawVignette(Graphics2D graphics) {
        // Create radial gradient from transparent to black
        RadialGradientPaint vignette = new RadialGradientPaint(
                new Point2D.Float(screenWidth/2f, screenHeight/2f),
                screenWidth * 0.7f,
                new float[] {0.0f, 0.7f, 1.0f},
                new Color[] {
                        new Color(0, 0, 0, 0),
                        new Color(0, 0, 0, 0),
                        new Color(0, 0, 0, 128)
                }
        );

        Paint oldPaint = graphics.getPaint();
        graphics.setPaint(vignette);
        graphics.fillRect(0, 0, screenWidth, screenHeight);
        graphics.setPaint(oldPaint);
    }

    // Energy field class - creates flowing energy patterns
    private class EnergyField {
        float x, y, size;
        Color color;
        float speed;
        float phase = 0;

        public EnergyField(float x, float y, float size, Color color, float speed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.speed = speed;
            this.phase = Util.random(0, (float)Math.PI * 2);
        }

        public void update(float dt, float time) {
            // Slow circular motion
            float radius = 50;
            x += Math.cos(time * speed + phase) * radius * dt;
            y += Math.sin(time * speed + phase) * radius * dt;

            // Ensure fields stay somewhat on screen
            if (x < -size) x = screenWidth + size/2;
            if (x > screenWidth + size) x = -size/2;
            if (y < -size) y = screenHeight + size/2;
            if (y > screenHeight + size) y = -size/2;
        }

        public void render(Graphics2D graphics) {
            // Create alpha composite for blending
            Composite oldComposite = graphics.getComposite();
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));

            // Create pulsating gradient field
            float pulse = 0.8f + 0.2f * (float)Math.sin(phase + time * 2);
            RadialGradientPaint fieldGradient = new RadialGradientPaint(
                    new Point2D.Float(x, y),
                    size * pulse,
                    new float[] {0.0f, 0.3f, 0.7f, 1.0f},
                    new Color[] {
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 180),
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 120),
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 60),
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                    }
            );

            Paint oldPaint = graphics.getPaint();
            graphics.setPaint(fieldGradient);

            // Draw field as a circle
            graphics.fillOval(
                    (int)(x - size/2 * pulse),
                    (int)(y - size/2 * pulse),
                    (int)(size * pulse),
                    (int)(size * pulse)
            );

            // Draw inner bright core
            float coreSize = size * 0.1f;
            graphics.setColor(new Color(255, 255, 255, 150));
            graphics.fillOval(
                    (int)(x - coreSize/2),
                    (int)(y - coreSize/2),
                    (int)coreSize,
                    (int)coreSize
            );

            // Restore original settings
            graphics.setPaint(oldPaint);
            graphics.setComposite(oldComposite);
        }
    }

    // Quantum particle class - small moving particles
    private class QuantumParticle {
        float x, y, size;
        Color color;
        float speed;
        float dirX, dirY;
        float phaseOffset;
        boolean entangled = false;
        QuantumParticle entangledPartner = null;

        public QuantumParticle(float x, float y, float size, Color color, float speed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.speed = speed;

            // Random direction
            float angle = Util.random(0, (float)(Math.PI * 2));
            dirX = (float)Math.cos(angle);
            dirY = (float)Math.sin(angle);

            phaseOffset = Util.random(0, 10);

            // Small chance of particle entanglement
            entangled = Math.random() < 0.2;
        }

        public void update(float dt, float time) {
            // Particles follow probability-like paths
            float t = time + phaseOffset;

            // Direction changes in a wave-like pattern
            float angleChange = (float)Math.sin(t * 1.5f) * 0.1f;
            float newAngle = (float)Math.atan2(dirY, dirX) + angleChange;

            dirX = (float)Math.cos(newAngle);
            dirY = (float)Math.sin(newAngle);

            // Move particle
            x += dirX * speed * dt;
            y += dirY * speed * dt;

            // Handle entangled particles
            if (entangled && entangledPartner != null) {
                // Occasionally teleport to match partner's state
                if (Math.random() < 0.01) {
                    float tempX = x;
                    float tempY = y;
                    x = entangledPartner.x;
                    y = entangledPartner.y;
                    entangledPartner.x = tempX;
                    entangledPartner.y = tempY;
                }
            }
        }

        public void render(Graphics2D graphics) {
            // Particles have glowing effect
            Composite oldComposite = graphics.getComposite();

            // Draw glow
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

            RadialGradientPaint glowGradient = new RadialGradientPaint(
                    new Point2D.Float(x, y),
                    size * 2,
                    new float[] {0.0f, 1.0f},
                    new Color[] {
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 100),
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                    }
            );

            Paint oldPaint = graphics.getPaint();
            graphics.setPaint(glowGradient);
            graphics.fillOval(
                    (int)(x - size * 2),
                    (int)(y - size * 2),
                    (int)(size * 4),
                    (int)(size * 4)
            );

            // Draw particle core
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            graphics.setColor(color);
            graphics.fillOval(
                    (int)(x - size/2),
                    (int)(y - size/2),
                    (int)size,
                    (int)size
            );

            // Draw entanglement line if applicable
            if (entangled && entangledPartner != null) {
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        0, new float[]{5, 5}, 0));
                graphics.drawLine((int)x, (int)y, (int)entangledPartner.x, (int)entangledPartner.y);
            }

            // Restore original settings
            graphics.setPaint(oldPaint);
            graphics.setComposite(oldComposite);
        }
    }

    // Wave pattern class - probability wave visualization
    private class WavePattern {
        float x, y;
        float amplitude, wavelength, speed;
        Color color;
        float phase = 0;

        public WavePattern(float x, float y, float amplitude, float wavelength, float speed, Color color) {
            this.x = x;
            this.y = y;
            this.amplitude = amplitude;
            this.wavelength = wavelength;
            this.speed = speed;
            this.color = color;
            this.phase = Util.random(0, (float)Math.PI * 2);
        }

        public void update(float dt, float time) {
            // Waves slowly drift across screen
            x -= speed * 10 * dt;
            if (x < -wavelength) {
                x = screenWidth + wavelength;
                y = Util.random(0, screenHeight);
            }
        }

        public void render(Graphics2D graphics) {
            // Create a sine wave path
            Path2D wavePath = new Path2D.Float();

            wavePath.moveTo(x, y);

            int segments = (int)(wavelength / 5); // Number of segments to draw
            float dx = wavelength / segments;

            for (int i = 0; i <= segments; i++) {
                float xPos = x + i * dx;
                float yPos = y + amplitude * (float)Math.sin((xPos / wavelength) * Math.PI * 2 + phase + time * speed);
                wavePath.lineTo(xPos, yPos);
            }

            // Draw the wave with transparency and interpolated color
            Composite oldComposite = graphics.getComposite();
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(wavePath);

            // Draw wave particles along the path
            int particleCount = 8;
            for (int i = 0; i < particleCount; i++) {
                float t = (float)i / particleCount;
                float xPos = x + t * wavelength;
                float yPos = y + amplitude * (float)Math.sin((xPos / wavelength) * Math.PI * 2 + phase + time * speed);

                float particleSize = 4 * (float)Math.pow(Math.sin(t * Math.PI), 2) + 2;

                graphics.setColor(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        (int)(255 * (0.5f + 0.5f * Math.sin(t * Math.PI)))
                ));

                graphics.fillOval(
                        (int)(xPos - particleSize/2),
                        (int)(yPos - particleSize/2),
                        (int)particleSize,
                        (int)particleSize
                );
            }

            // Restore original composite
            graphics.setComposite(oldComposite);
        }
    }
}
