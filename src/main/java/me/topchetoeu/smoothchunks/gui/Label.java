package me.topchetoeu.smoothchunks.gui;

import org.apache.commons.lang3.Validate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper.Argb;

public class Label extends DrawableHelper implements Drawable, Element, BoundboxProvider {
    public final MinecraftClient client;

    private Text text = Text.of("");
    private int height = 0;
    public int paddingX = 0, paddingY = 0;
    public int x, y;
    public int maxWidth;

    public Label setMaxWidth(int val) {
        maxWidth = val;
        return this;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public float getWidth() {
        if (maxWidth <= 0) return 2 + paddingX * 2 + client.textRenderer.getWidth(text);
        else return 2 + paddingX * 2 + maxWidth;
    }
    public float getHeight() {
        return 1 + paddingY * 2 + height;
    }

    public Text getText() {
        return text;
    }
    public void setText(Text text) {
        Validate.notNull(text, "text may not be null.");
        this.text = text;
    }
    public void setText(String text) {
        Validate.notNull(text, "text may not be null.");
        this.text = Text.of(text);
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float delta) {
        int white = Argb.getArgb(255, 255, 255, 255);
        matrices.push();
        matrices.translate(this.x, this.y, getZOffset());

        if (maxWidth <= 0) {
            client.textRenderer.draw(matrices, text, paddingX + 1, paddingY + 1, white);
            height = client.textRenderer.fontHeight;
        }
        else {
            height = (int)SelectionScreen.drawWarpedText(client.textRenderer, matrices, text, paddingX + 1, paddingY + 1, maxWidth);
        }

        matrices.pop();
    }

    public Label(int x, int y, Text text) {
        this.client = MinecraftClient.getInstance();
        setText(text);
        this.x = x;
        this.y = y;
    }
}
