package me.topchetoeu.animatedchunks.gui;

import net.minecraft.client.gui.ParentElement;

public class NumberInput extends Input {
    public interface InputAction {
        void input(NumberInput sender, float number);
    }
    private float value;
    public InputAction action;

    public float getValue() {
        return value;
    }
    public NumberInput setValue(float val) {
        value = val;
        action.input(this, val);
        return this;
    }

    public NumberInput(ParentElement parent, int x, int y, float value, InputAction action) {
        super(parent, x, y, null);
        super.action = (sender, val) -> {
            try {
                invalid = false;
                setValue(Float.parseFloat(val.trim()));
            }
            catch (NumberFormatException e) {
                invalid = true;
            }
        };
        this.action = action;
        setContent(Float.toString(value));
    }
    public NumberInput(ParentElement parent, int x, int y, InputAction action) {
        this(parent, x, y, 0, action);
    }
}
