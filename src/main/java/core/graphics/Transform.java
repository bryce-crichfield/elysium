package core.graphics;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class Transform {
    private final Matrix4f matrix;

    public Transform() {
        this.matrix = new Matrix4f();
        this.matrix.identity();
    }

    public Transform(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public static Transform fromScreenSpace(int width, int height) {
        Transform transform = new Transform();
        transform.translate(-1.0f, 1.0f);  // Change Y from -1 to 1
        transform.scale(2.0f / width, -2.0f / height);  // Negate Y scale
        return transform;
    }

    public static Transform createTranslate(float x, float y) {
        Transform transform = new Transform();
        transform.translate(x, y);
        return transform;
    }

    public Transform translate(Number x, Number y) {
        matrix.translate(x.floatValue(), y.floatValue(), 0);
        return this;
    }

    public Transform scale(Number v, Number v1) {
        matrix.scale(v.floatValue(), v1.floatValue(), 1);
        return this;
    }

    public Transform compose(Transform other) {
        Matrix4f result = new Matrix4f();
        matrix.mul(other.matrix, result);
        return new Transform(result);
    }

    public Vector2f apply(Vector2f point) {
        Vector3f result = new Vector3f();
        // matrix * point
        matrix.transformPosition(point.x, point.y, 0, result);
        return new Vector2f(result.x, result.y);
    }

    public Transform copy() {
        return new Transform(new Matrix4f(matrix));
    }

    public Transform inverse() {
        Matrix4f inverseMatrix = new Matrix4f();
        matrix.invert(inverseMatrix);
        return new Transform(inverseMatrix);
    }


    public static Transform orthographic(float left, float right, float bottom, float top, float near, float far) {
        float[] matrix = new float[16];
        // Clear matrix
        for (int i = 0; i < 16; i++) {
            matrix[i] = 0;
        }

        matrix[0] = 2.0f / (right - left);
        matrix[5] = 2.0f / (top - bottom);
        matrix[10] = -2.0f / (far - near);
        matrix[12] = -(right + left) / (right - left);
        matrix[13] = -(top + bottom) / (top - bottom);
        matrix[14] = -(far + near) / (far - near);
        matrix[15] = 1.0f;

        return new Transform(new Matrix4f().set(matrix));
    }
}
