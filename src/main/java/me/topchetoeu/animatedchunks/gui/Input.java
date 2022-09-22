package me.topchetoeu.animatedchunks.gui;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper.Argb;

public class Input extends DrawableHelper implements Drawable, Element, Selectable, BoundboxProvider {
    public static interface ClickAction {
        void onClick();
    }

    public ClickAction clickAction;
    public final MinecraftClient client;
    private boolean clicked = false;
    private boolean focused = false;
    private boolean hovered = false;

    public String content = "";
    public int paddingX = 5, paddingY = 2;
    public int x, y, width = 100;
    public float scale = 1;
    public float min = 0, max = 10;
    private float value;

    public float getValue() {
        return value;
    }
    public Input setValue(float val) {
        if (val < min) val = min;
        if (val > max) val = max;

        value = val;
        return this;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return paddingY * 2 + client.textRenderer.fontHeight;
    }

    public void click() {
        if (clickAction != null) clickAction.onClick();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder msgBuilder) {
        msgBuilder.put(NarrationPart.HINT, "Input");
    }

    @Override
    public SelectionType getType() {
        if (focused) return SelectionType.FOCUSED;
        if (hovered) return SelectionType.HOVERED;
        return SelectionType.NONE;
    }
    @Override
    public boolean changeFocus(boolean lookForwards) {
        focused = !focused;
        return focused;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float delta) {
        int white = Argb.getArgb(255, 255, 255, 255);
        matrices.push();
        matrices.translate(this.x, this.y, getZOffset());

        hovered = isMouseOver(x, y);

        // if (hovered) {
        //     fill(matrices, 0, 0, (int)getWidth(), (int)getHeight(), Argb.getArgb(32, 255, 255, 255));
        // }
        if (clicked) {
            fill(matrices, 0, 0, (int)getWidth(), (int)getHeight(), Argb.getArgb(127, 255, 255, 255));
        }

        drawHorizontalLine(matrices, 0, (int)getWidth() - 1, 0, white);
        drawVerticalLine(matrices, 0, 0, (int)getHeight() - 1, white);
        drawVerticalLine(matrices, (int)getWidth() - 1, 0, (int)getHeight() - 1, white);
        drawHorizontalLine(matrices, 0, (int)getWidth() - 1, (int)getHeight() - 1, white);

        // if (focused) {
        // }

        client.textRenderer.draw(matrices, content, paddingX + 1, paddingY + 1, white);

        matrices.pop();
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        if (clicked) return true;

        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        content += chr;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && content.length() > 0) content = content.substring(0, content.length() - 1);
        return false;
    }
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            clicked = false;
            return true;
        }
        return false;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        if (isMouseOver(mouseX - this.x, mouseY - this.y)) {
            clicked = true;
            hovered = true;
            return true;
        }
        return false;
        // return true;
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        if (clicked) {
            clicked = false;
            return true;
        }
        return false;
    }

    public Input(int x, int y, ClickAction clickAction) {
        this.clickAction = clickAction;
        this.client = MinecraftClient.getInstance();
        this.x = x;
        this.y = y;
    }
}