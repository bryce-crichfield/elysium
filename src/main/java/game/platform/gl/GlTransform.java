package game.platform.gl;

import game.platform.Transform;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class GlTransform implements Transform {
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

    public static GlTransform fromScreenSpace(int width, int height) {
        GlTransform transform = new GlTransform();
        transform.translate(-1.0f, 1.0f);  // Change Y from -1 to 1
        transform.scale(2.0f / width, -2.0f / height);  // Negate Y scale
        return transform;
    }

    public static GlTransform toScreenSpace(float screenWidth, float screenHeight) {
        // Converts from NDC to screen space
        GlTransform transform = new GlTransform();
        transform.scale(screenWidth / 2.0f, screenHeight / 2.0f);
        transform.translate(1.0f, -1.0f);  // Change Y from -1 to 1
        return transform;
    }

    public static GlTransform createTranslate(float x, float y) {
        GlTransform transform = new GlTransform();
        transform.translate(x, y);
        return transform;
    }

    public void scale(float v, float v1) {
        matrix.scale(v, v1, 1);
    }

    public Vector2f transform(Vector2f point) {
        Vector3f result = new Vector3f();
        // matrix * point
        matrix.transformPosition(point.x, point.y, 0, result);
        return new Vector2f(result.x, result.y);
    }

    @Override
    public Transform copy() {
        return new GlTransform(new Matrix4f(matrix));
    }

    @Override
    public Transform translate(int x, int y) {
        return new GlTransform(new Matrix4f(matrix).translate(x, y, 0));
    }

    @Override
    public Transform inverse() {
        Matrix4f inverseMatrix = new Matrix4f();
        matrix.invert(inverseMatrix);
        return new GlTransform(inverseMatrix);
    }
}
