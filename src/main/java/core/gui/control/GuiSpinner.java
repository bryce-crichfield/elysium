package core.gui.control;

import core.gui.container.GuiContainer;
import core.gui.layout.GuiHorizontalLayout;
import core.gui.style.GuiBackground;
import core.gui.style.GuiBorder;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class GuiSpinner<T> extends GuiContainer {
    private final GuiButton backBtn;
    private final GuiButton forwardBtn;
    private final GuiLabel valueLabel;
    @Getter
    private Optional<T> value  = Optional.empty();
    private List<T> values  = List.of();
    int index = 0;

    public GuiSpinner(int x, int y, int width, int height) {
        super(x, y, width, height);
        setLayout(new GuiHorizontalLayout());

        backBtn = new GuiButton("<", 30, 30, this::decrement);
        forwardBtn = new GuiButton(">", 30, 30,this::increment);
        valueLabel = new GuiLabel(100, 30, "");
        var labelStyle = valueLabel.getStyle();
        labelStyle = labelStyle.withBorder(new GuiBorder(Color.WHITE, 2));
        labelStyle = labelStyle.withBackground(new GuiBackground.Fill(Color.BLACK));
        valueLabel.setStyle(labelStyle);

        addChild(backBtn);
        addChild(valueLabel);
        addChild(forwardBtn);
    }

    public void setValue(T value) {
        this.value = Optional.of(value);
        index = values.indexOf(value);
        valueLabel.setText(value.toString());
    }

    public void setValues(List<T> values) {
        this.values = values;
        if (values.isEmpty()) {
            valueLabel.setText("");
        } else {
            index = 0;
            value = Optional.of(values.get(index));
            valueLabel.setText(value.get().toString());
        }
    }

    private void decrement() {
        if (values.isEmpty()) return;
        index = (index - 1 + values.size()) % values.size();
        value = Optional.of(values.get(index));
        valueLabel.setText(value.get().toString());
    }

    private void increment() {
        if (values.isEmpty()) return;
        index = (index + 1) % values.size();
        value = Optional.of(values.get(index));
        valueLabel.setText(value.get().toString());
    }
}
