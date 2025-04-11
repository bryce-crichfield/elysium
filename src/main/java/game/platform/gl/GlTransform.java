package game.platform.gl;

import game.platform.Transform;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class GlTransform extends Transform {
    private final Matrix4f matrix;

    public GlTransform() {
        this.matrix = new Matrix4f();
        this.matrix.identity();
    }

    public GlTransform(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public void translate(float x, float y) {
        matrix.translate(x, y, 0);
    }

    public GlTransform compose(GlTransform other) {
        Matrix4f result = new Matrix4f();
        matrix.mul(other.matrix, result);
        return new GlTransform(result);
    }

    public static GlTransform fromScreenSpace(int screenWidth, int screenHeight) {
        var screenToNdcMatrix = new Matrix4f();

        // Create identity matrix
        screenToNdcMatrix.identity();

        // Operations are applied in reverse order of how they're written
        // So we start with the last operation to be applied

        // 3. Scale to [-1, 1] range (applied first)
        screenToNdcMatrix.scale(2.0f / screenWidth, 2.0f / screenHeight, 1.0f);

        // 2. Translate origin (applied second)
        screenToNdcMatrix.translate(-1.0f, -1.0f, 0.0f);

        return new GlTransform(screenToNdcMatrix);
    }

    public static GlTransform toScreenSpace(int screenWidth, int screenHeight) {
        // Create a new matrix
        var ndcToScreenMatrix = new Matrix4f();

        // Scale from [-1, 1] to [0, width/height]
        // First scale by 0.5 to get range [-0.5, 0.5]
        ndcToScreenMatrix.scale(0.5f);

        // Then translate by (0.5, 0.5) to get range [0, 1]
        ndcToScreenMatrix.translate(new Vector3f(1.0f, 1.0f, 0.0f));

        // Finally scale by screen dimensions
        ndcToScreenMatrix.scale(new Vector3f(screenWidth, screenHeight, 1.0f));

        return new GlTransform(ndcToScreenMatrix);
    }

    public Vector2f transform(Vector2f point) {
        Vector3f result = new Vector3f();
        // matrix * point
        matrix.transformPosition(point.x, point.y, 0, result);
        return new Vector2f(result.x, result.y);
    }

}
