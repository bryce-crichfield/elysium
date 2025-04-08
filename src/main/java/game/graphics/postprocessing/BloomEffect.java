package game.graphics.postprocessing;

import java.awt.image.*;

public class BloomEffect implements PostProcessor {
    private float threshold = 0.7f;
    private float intensity = 0.3f;
    private int blurRadius = 5;
    private boolean enabled = true;

    // Pre-computed kernels and lookup tables
    private float[] gaussianKernel;
    private BufferedImage downscaledBuffer;
    private BufferedImage tempBuffer;
    private boolean kernelNeedsUpdate = true;

    // Reusable data arrays to avoid GC pressure
    private int[] inputPixels;
    private int[] outputPixels;
    private int[] tempPixels;

    public BloomEffect() {
        updateKernel();
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setBlurRadius(int blurRadius) {
        if (this.blurRadius != blurRadius) {
            this.blurRadius = blurRadius;
            kernelNeedsUpdate = true;
        }
    }

    private void updateKernel() {
        gaussianKernel = createGaussianKernel(blurRadius);
        kernelNeedsUpdate = false;
    }

    @Override
    public void process(BufferedImage input, BufferedImage output) {
        if (kernelNeedsUpdate) {
            updateKernel();
        }

        int width = input.getWidth();
        int height = input.getHeight();

        // Create or resize our reusable buffers if needed
        ensureBuffersExist(width, height);

        // Extract bright areas at half resolution for better performance
        extractBrightAreasDownscaled(input, downscaledBuffer, threshold);

        // Blur the bright areas with an optimized approach
//        applyFastGaussianBlur(downscaledBuffer, tempBuffer, downscaledBuffer);

        // Box blur is faster and simpler for this use case
        applyBoxBlur(downscaledBuffer, tempBuffer, blurRadius);
        // Combine original and blurred bright areas using fast direct pixel manipulation
        combineImages(input, downscaledBuffer, output);
    }

    private void ensureBuffersExist(int width, int height) {
        // Use half resolution for the bloom effect (big performance win with minimal quality loss)
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        if (downscaledBuffer == null || downscaledBuffer.getWidth() != halfWidth || downscaledBuffer.getHeight() != halfHeight) {
            downscaledBuffer = new BufferedImage(halfWidth, halfHeight, BufferedImage.TYPE_INT_ARGB);
            tempBuffer = new BufferedImage(halfWidth, halfHeight, BufferedImage.TYPE_INT_ARGB);

            // Initialize pixel arrays
            inputPixels = new int[width * height];
            outputPixels = new int[width * height];
            tempPixels = new int[halfWidth * halfHeight];
        }
    }

    private void extractBrightAreasDownscaled(BufferedImage input, BufferedImage brightAreas, float threshold) {
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int outputWidth = brightAreas.getWidth();
        int outputHeight = brightAreas.getHeight();

        // Threshold value (0.0-1.0) converted to 0-255 range
        int thresholdValue = (int)(threshold * 255);

        // Get input pixels
        input.getRGB(0, 0, inputWidth, inputHeight, inputPixels, 0, inputWidth);

        // Process and downsample in one pass
        for (int y = 0; y < outputHeight; y++) {
            for (int x = 0; x < outputWidth; x++) {
                // Map to input coordinates (with simple 2x2 downsampling)
                int inputX = x * 2;
                int inputY = y * 2;

                // Read source pixel (skip edge cases)
                if (inputX >= inputWidth || inputY >= inputHeight) {
                    brightAreas.setRGB(x, y, 0);
                    continue;
                }

                int pixelIndex = inputY * inputWidth + inputX;
                int rgb = inputPixels[pixelIndex];

                // Extract color components
                int alpha = (rgb >> 24) & 0xFF;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Skip fully transparent pixels
                if (alpha == 0) {
                    brightAreas.setRGB(x, y, 0);
                    continue;
                }

                // Fast brightness calculation - using bit shifting for performance
                // (R + G + B) / 3 approximated as (R + G + B) / 4 + (R + G + B) / 16 for speed
                int brightness = (red + green + blue);
                brightness = (brightness >> 2) + (brightness >> 4); // Approx. divide by 3

                if (brightness > thresholdValue) {
                    // Calculate how much brighter than threshold
                    float factor = (brightness - thresholdValue) / (float)(255 - thresholdValue);

                    // Scale the color based on how bright it is above the threshold
                    // Use fast integer math instead of floating point
                    int newRed = (int)(factor * red);
                    int newGreen = (int)(factor * green);
                    int newBlue = (int)(factor * blue);

                    // Combine back into ARGB
                    int newRgb = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                    brightAreas.setRGB(x, y, newRgb);
                } else {
                    // Below threshold - set to transparent black
                    brightAreas.setRGB(x, y, 0);
                }
            }
        }
    }

    private void applyFastGaussianBlur(BufferedImage input, BufferedImage temp, BufferedImage output) {
        int width = input.getWidth();
        int height = input.getHeight();

        // Get pixels once
        DataBufferInt dataBuffer = (DataBufferInt) input.getRaster().getDataBuffer();
        int[] inPixels = dataBuffer.getData();

        DataBufferInt tempDataBuffer = (DataBufferInt) temp.getRaster().getDataBuffer();
        int[] tempPixels = tempDataBuffer.getData();

        DataBufferInt outDataBuffer = (DataBufferInt) output.getRaster().getDataBuffer();
        int[] outPixels = outDataBuffer.getData();

        int radius = blurRadius;

        // Horizontal pass - directly access pixel arrays for speed
        for (int y = 0; y < height; y++) {
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {
                int r = 0, g = 0, b = 0, a = 0;
                float weightSum = 0;

                for (int i = -radius; i <= radius; i++) {
                    int ix = Math.min(Math.max(x + i, 0), width - 1);
                    int pixelIndex = rowOffset + ix;

                    int pixel = inPixels[pixelIndex];
                    float weight = gaussianKernel[i + radius];

                    a += ((pixel >> 24) & 0xFF) * weight;
                    r += ((pixel >> 16) & 0xFF) * weight;
                    g += ((pixel >> 8) & 0xFF) * weight;
                    b += (pixel & 0xFF) * weight;
                    weightSum += weight;
                }

                // Fast rounding: (int)(x + 0.5f) == (int)(x + 0.5)
                int avg_a = (int)(a / weightSum + 0.5f);
                int avg_r = (int)(r / weightSum + 0.5f);
                int avg_g = (int)(g / weightSum + 0.5f);
                int avg_b = (int)(b / weightSum + 0.5f);

                // Combine back into ARGB without bounds checking (we know values are in range)
                tempPixels[rowOffset + x] = (avg_a << 24) | (avg_r << 16) | (avg_g << 8) | avg_b;
            }
        }

        // Vertical pass
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = 0, g = 0, b = 0, a = 0;
                float weightSum = 0;

                for (int i = -radius; i <= radius; i++) {
                    int iy = Math.min(Math.max(y + i, 0), height - 1);
                    int pixelIndex = iy * width + x;

                    int pixel = tempPixels[pixelIndex];
                    float weight = gaussianKernel[i + radius];

                    a += ((pixel >> 24) & 0xFF) * weight;
                    r += ((pixel >> 16) & 0xFF) * weight;
                    g += ((pixel >> 8) & 0xFF) * weight;
                    b += (pixel & 0xFF) * weight;
                    weightSum += weight;
                }

                int avg_a = (int)(a / weightSum + 0.5f);
                int avg_r = (int)(r / weightSum + 0.5f);
                int avg_g = (int)(g / weightSum + 0.5f);
                int avg_b = (int)(b / weightSum + 0.5f);

                outPixels[y * width + x] = (avg_a << 24) | (avg_r << 16) | (avg_g << 8) | avg_b;
            }
        }
    }

    private void combineImages(BufferedImage original, BufferedImage bloom, BufferedImage output) {
        int origWidth = original.getWidth();
        int origHeight = original.getHeight();
        int bloomWidth = bloom.getWidth();
        int bloomHeight = bloom.getHeight();

        // Get original image pixels
        original.getRGB(0, 0, origWidth, origHeight, inputPixels, 0, origWidth);
        System.arraycopy(inputPixels, 0, outputPixels, 0, inputPixels.length);

        // Scale factor for upsampling
        float xScale = (float)bloomWidth / origWidth;
        float yScale = (float)bloomHeight / origHeight;

        // Apply bloom with fast bilinear upsampling
        for (int y = 0; y < origHeight; y++) {
            int origY = y * origWidth;
            float bloomY = y * yScale;
            int bloomYi = (int)bloomY;
            float yFraction = bloomY - bloomYi;

            for (int x = 0; x < origWidth; x++) {
                // Bilinear interpolation for smooth upscaling
                float bloomX = x * xScale;
                int bloomXi = (int)bloomX;
                float xFraction = bloomX - bloomXi;

                // Ensure we're in bounds
                int x0 = Math.min(bloomXi, bloomWidth - 1);
                int y0 = Math.min(bloomYi, bloomHeight - 1);
                int x1 = Math.min(x0 + 1, bloomWidth - 1);
                int y1 = Math.min(y0 + 1, bloomHeight - 1);

                // Get the four nearest pixels
                int rgb00 = bloom.getRGB(x0, y0);
                int rgb10 = bloom.getRGB(x1, y0);
                int rgb01 = bloom.getRGB(x0, y1);
                int rgb11 = bloom.getRGB(x1, y1);

                // Interpolate alpha
                int a00 = (rgb00 >> 24) & 0xFF;
                int a10 = (rgb10 >> 24) & 0xFF;
                int a01 = (rgb01 >> 24) & 0xFF;
                int a11 = (rgb11 >> 24) & 0xFF;

                int a0 = (int)(a00 * (1 - xFraction) + a10 * xFraction);
                int a1 = (int)(a01 * (1 - xFraction) + a11 * xFraction);
                int a = (int)(a0 * (1 - yFraction) + a1 * yFraction);

                // Early skip if transparent
                if (a == 0) continue;

                // Interpolate red
                int r00 = (rgb00 >> 16) & 0xFF;
                int r10 = (rgb10 >> 16) & 0xFF;
                int r01 = (rgb01 >> 16) & 0xFF;
                int r11 = (rgb11 >> 16) & 0xFF;

                int r0 = (int)(r00 * (1 - xFraction) + r10 * xFraction);
                int r1 = (int)(r01 * (1 - xFraction) + r11 * xFraction);
                int r = (int)(r0 * (1 - yFraction) + r1 * yFraction);

                // Interpolate green
                int g00 = (rgb00 >> 8) & 0xFF;
                int g10 = (rgb10 >> 8) & 0xFF;
                int g01 = (rgb01 >> 8) & 0xFF;
                int g11 = (rgb11 >> 8) & 0xFF;

                int g0 = (int)(g00 * (1 - xFraction) + g10 * xFraction);
                int g1 = (int)(g01 * (1 - xFraction) + g11 * xFraction);
                int g = (int)(g0 * (1 - yFraction) + g1 * yFraction);

                // Interpolate blue
                int b00 = rgb00 & 0xFF;
                int b10 = rgb10 & 0xFF;
                int b01 = rgb01 & 0xFF;
                int b11 = rgb11 & 0xFF;

                int b0 = (int)(b00 * (1 - xFraction) + b10 * xFraction);
                int b1 = (int)(b01 * (1 - xFraction) + b11 * xFraction);
                int b = (int)(b0 * (1 - yFraction) + b1 * yFraction);

                // Get the original pixel
                int idx = origY + x;
                int origRgb = outputPixels[idx];

                // Extract original components
                int origR = (origRgb >> 16) & 0xFF;
                int origG = (origRgb >> 8) & 0xFF;
                int origB = origRgb & 0xFF;

                // Blend with intensity
                int blendedR = Math.min(255, origR + (int)(r * intensity));
                int blendedG = Math.min(255, origG + (int)(g * intensity));
                int blendedB = Math.min(255, origB + (int)(b * intensity));

                // Combine back into RGB (preserve original alpha)
                outputPixels[idx] = (origRgb & 0xFF000000) | (blendedR << 16) | (blendedG << 8) | blendedB;
            }
        }

        // Set final pixels to output
        output.setRGB(0, 0, origWidth, origHeight, outputPixels, 0, origWidth);
    }

    private float[] createGaussianKernel(int radius) {
        int size = radius * 2 + 1;
        float[] kernel = new float[size];

        // Standard deviation (sigma)
        float sigma = radius / 2.0f; // Using 2.0 instead of 3.0 for a more compact kernel
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float) Math.sqrt(2.0f * Math.PI * sigma * sigma);
        float total = 0.0f;

        // Calculate un-normalized Gaussian values
        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            int index = i + radius;
            kernel[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += kernel[index];
        }

        // Normalize the kernel
        for (int i = 0; i < size; i++) {
            kernel[i] /= total;
        }

        return kernel;
    }

    // For even better performance, we could use a Box Blur approximation
    // A 3-pass box blur can approximate a Gaussian pretty well and is much faster
    private void applyBoxBlur(BufferedImage input, BufferedImage output, int radius) {
        int width = input.getWidth();
        int height = input.getHeight();

        // Get direct access to pixel data
        DataBufferInt inputBuffer = (DataBufferInt) input.getRaster().getDataBuffer();
        int[] inPixels = inputBuffer.getData();

        DataBufferInt outputBuffer = (DataBufferInt) output.getRaster().getDataBuffer();
        int[] outPixels = outputBuffer.getData();

        // Create a temporary buffer for intermediate results
        int[] tempPixels = new int[width * height];

        // Three-pass box blur approximates a Gaussian blur
        // Each pass is a separable box blur (horizontal + vertical)

        // First pass - Horizontal
        for (int y = 0; y < height; y++) {
            int rowOffset = y * width;

            // Initialize running sum for the first pixel
            int r_sum = 0, g_sum = 0, b_sum = 0, a_sum = 0;

            // Fill the initial window
            for (int i = -radius; i <= radius; i++) {
                int x = Math.max(0, Math.min(i, width - 1));
                int pixel = inPixels[rowOffset + x];

                a_sum += (pixel >> 24) & 0xFF;
                r_sum += (pixel >> 16) & 0xFF;
                g_sum += (pixel >> 8) & 0xFF;
                b_sum += pixel & 0xFF;
            }

            // Compute first pixel
            int windowSize = radius * 2 + 1;
            int a = a_sum / windowSize;
            int r = r_sum / windowSize;
            int g = g_sum / windowSize;
            int b = b_sum / windowSize;
            tempPixels[rowOffset] = (a << 24) | (r << 16) | (g << 8) | b;

            // Sliding window for remaining pixels
            for (int x = 1; x < width; x++) {
                // Remove leftmost pixel
                int removeX = Math.max(0, x - radius - 1);
                int removePixel = inPixels[rowOffset + removeX];

                a_sum -= (removePixel >> 24) & 0xFF;
                r_sum -= (removePixel >> 16) & 0xFF;
                g_sum -= (removePixel >> 8) & 0xFF;
                b_sum -= removePixel & 0xFF;

                // Add rightmost pixel
                int addX = Math.min(width - 1, x + radius);
                int addPixel = inPixels[rowOffset + addX];

                a_sum += (addPixel >> 24) & 0xFF;
                r_sum += (addPixel >> 16) & 0xFF;
                g_sum += (addPixel >> 8) & 0xFF;
                b_sum += addPixel & 0xFF;

                // Compute output
                a = a_sum / windowSize;
                r = r_sum / windowSize;
                g = g_sum / windowSize;
                b = b_sum / windowSize;
                tempPixels[rowOffset + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        // Second pass - Vertical
        for (int x = 0; x < width; x++) {
            // Initialize running sum for the first pixel
            int r_sum = 0, g_sum = 0, b_sum = 0, a_sum = 0;

            // Fill the initial window
            for (int i = -radius; i <= radius; i++) {
                int y = Math.max(0, Math.min(i, height - 1));
                int pixel = tempPixels[y * width + x];

                a_sum += (pixel >> 24) & 0xFF;
                r_sum += (pixel >> 16) & 0xFF;
                g_sum += (pixel >> 8) & 0xFF;
                b_sum += pixel & 0xFF;
            }

            // Compute first pixel
            int windowSize = radius * 2 + 1;
            int a = a_sum / windowSize;
            int r = r_sum / windowSize;
            int g = g_sum / windowSize;
            int b = b_sum / windowSize;
            outPixels[x] = (a << 24) | (r << 16) | (g << 8) | b;

            // Sliding window for remaining pixels
            for (int y = 1; y < height; y++) {
                // Remove topmost pixel
                int removeY = Math.max(0, y - radius - 1);
                int removePixel = tempPixels[removeY * width + x];

                a_sum -= (removePixel >> 24) & 0xFF;
                r_sum -= (removePixel >> 16) & 0xFF;
                g_sum -= (removePixel >> 8) & 0xFF;
                b_sum -= removePixel & 0xFF;

                // Add bottommost pixel
                int addY = Math.min(height - 1, y + radius);
                int addPixel = tempPixels[addY * width + x];

                a_sum += (addPixel >> 24) & 0xFF;
                r_sum += (addPixel >> 16) & 0xFF;
                g_sum += (addPixel >> 8) & 0xFF;
                b_sum += addPixel & 0xFF;

                // Compute output
                a = a_sum / windowSize;
                r = r_sum / windowSize;
                g = g_sum / windowSize;
                b = b_sum / windowSize;
                outPixels[y * width + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        // For a better Gaussian approximation, we could run a third pass with adjusted radius
        // This implementation uses two passes for simplicity and efficiency
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}