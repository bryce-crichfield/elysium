package game.graphics.postprocessing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class VignetteEffect implements PostProcessor {
    private float intensity; // 0.0 (no effect) to 1.0 (max darkening)
    private float radius;    // 0.0 (center only) to 1.0 (reaches corners)
    private boolean enabled = true;

    /**
     * Creates a vignette post-processing effect
     *
     * @param radius How far the vignette extends from the center (0.0-1.0)
     * @param intensity How dark the vignette effect is (0.0-1.0)
     */
    public VignetteEffect(float radius, float intensity) {
        this.radius = Math.max(0.0f, Math.min(1.0f, radius));
        this.intensity = Math.max(0.0f, Math.min(1.0f, intensity));
    }

    @Override
    public void process(BufferedImage input, BufferedImage output) {
        int width = input.getWidth();
        int height = input.getHeight();

        // Draw the original image to the output first
        Graphics2D g = output.createGraphics();
        g.drawImage(input, 0, 0, null);

        // Create a radial gradient for the vignette
        Point2D center = new Point2D.Float(width / 2.0f, height / 2.0f);

        // Calculate max distance from center to corner
        float maxDistance = (float) Math.sqrt(
                Math.pow(Math.max(center.getX(), width - center.getX()), 2) +
                        Math.pow(Math.max(center.getY(), height - center.getY()), 2)
        );

        // Adjust radius relative to the image dimensions
        float actualRadius = maxDistance * radius;

        // Create gradient paint
        Color transparentBlack = new Color(0, 0, 0, 0);
        Color vignetteColor = new Color(0, 0, 0, (int)(255 * intensity));

        RadialGradientPaint paint = new RadialGradientPaint(
                center,
                actualRadius,
                new float[] {0.0f, 0.7f, 1.0f},
                new Color[] {transparentBlack, transparentBlack, vignetteColor}
        );

        // Draw the vignette
        g.setPaint(paint);
        g.fillRect(0, 0, width, height);
        g.dispose();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Set the intensity of the vignette darkening
     * @param intensity Value between 0.0 (no darkening) and 1.0 (maximum darkening)
     */
    public void setIntensity(float intensity) {
        this.intensity = Math.max(0.0f, Math.min(1.0f, intensity));
    }

    /**
     * Set how far the vignette extends from the center
     * @param radius Value between 0.0 (center only) and 1.0 (reaches corners)
     */
    public void setRadius(float radius) {
        this.radius = Math.max(0.0f, Math.min(1.0f, radius));
    }
}