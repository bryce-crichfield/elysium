package sampleGame.data.entity.component;

import java.io.Serializable;

public interface Component extends Serializable {
    Component clone();
}
