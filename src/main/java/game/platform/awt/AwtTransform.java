package game.platform.awt;

import game.platform.Transform;
import lombok.Getter;

import java.awt.geom.AffineTransform;

public class AwtTransform extends Transform {
    @Getter
    private final AffineTransform transform;

    public AwtTransform(AffineTransform transform) {
        this.transform = transform;
    }
}
