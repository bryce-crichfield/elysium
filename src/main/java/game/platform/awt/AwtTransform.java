package game.platform.awt;

import game.platform.Transform;
import lombok.Getter;

import java.awt.geom.AffineTransform;

public class AwtTransform implements Transform {
    @Getter
    private final AffineTransform transform;

    public AwtTransform(AffineTransform transform) {
        this.transform = transform;
    }

    @Override
    public Transform copy() {
        return new AwtTransform((AffineTransform) transform.clone());
    }

    @Override
    public Transform translate(int x, int y) {
        transform.translate(x, y);
        return this;
    }

    @Override
    public Transform inverse() {
        try {
            AffineTransform inverseTransform = transform.createInverse();
            return new AwtTransform(inverseTransform);
        } catch (java.awt.geom.NoninvertibleTransformException e) {
            throw new RuntimeException("Cannot invert transform", e);
        }
    }
}
