package me.topchetoeu.animatedchunks.gui;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper.Argb;

public class Input extends DrawableHelper implements Drawable, Element, Selectable, BoundboxProvider {
    public interface InputAction {
        void onInput(Input sender, String val);
    }

    public final MinecraftClient client;
    private boolean clicked = false;
    private boolean hovered = false;
    private boolean isFocused() {
        return parent.getFocused() == this;
    }
    private int index = 0;

    private final ParentElement parent;
    private String content = "";
    public int paddingX = 5, paddingY = 2;
    private float time = 0;
    public int x, y, width = 100;
    public boolean invalid = false;
    public InputAction action = null;

    public String getContent() {
        return content;
    }
    public Input setContent(String val) {
        if (index > val.length()) index = val.length();
        content = val;
        action.onInput(this, val);
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
        return 1 + paddingY * 2 + client.textRenderer.fontHeight;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder msgBuilder) {
        msgBuilder.put(NarrationPart.HINT, "Input");
    }

    @Override
    public SelectionType getType() {
        if (isFocused()) return SelectionType.FOCUSED;
        if (hovered) return SelectionType.HOVERED;
        return SelectionType.NONE;
    }
    @Override
    public boolean changeFocus(boolean lookForwards) {
        if (isFocused()) {
            parent.setFocused(null);
            return false;
        }
        else {
            parent.setFocused(this);
            return true;
        }
    }

    private void renderCursor(MatrixStack matrices, float delta) {
        time += delta / 20;

        if ((int)(time * 2) % 2 != 0) return;

        // RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(SrcFactor.ONE_MINUS_DST_COLOR, DstFactor.ONE_MINUS_SRC_COLOR, SrcFactor.ONE, DstFactor.ZERO);

        float x1 = paddingX + client.textRenderer.getWidth(content.substring(0, index)) + 1;
        float x2 = x1 + 1;
        // if (index < content.length()) {
        //     x2 += client.textRenderer.getWidth("" + content.charAt(index)) - 2;
        // }
        float y1 = paddingY;
        float y2 = y1 + client.textRenderer.fontHeight;
    
        ChunkPreview.myFill(matrices, x1, y1, x2, y2, 1, 1, 1, 1);
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

        client.textRenderer.draw(matrices, content, paddingX + 1, paddingY + 1, white);
        if (isFocused()) renderCursor(matrices, delta);

        if (invalid) white = 0xFFFF0000;

        drawHorizontalLine(matrices, 0, (int)getWidth() - 1, 0, white);
        drawVerticalLine(matrices, 0, 0, (int)getHeight() - 1, white);
        drawVerticalLine(matrices, (int)getWidth() - 1, 0, (int)getHeight() - 1, white);
        drawHorizontalLine(matrices, 0, (int)getWidth() - 1, (int)getHeight() - 1, white);

        // if (focused) {
        // }

        matrices.pop();
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        if (clicked) return true;

        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        content = content.substring(0, index) + chr + content.substring(index);
        action.onInput(this, content);
        index++;
        time = 0;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && index > 0) {
            content = content.substring(0, index - 1) + content.substring(index);
            action.onInput(this, content);
            index--;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE && index < content.length()) {
            time = 0;
            content = content.substring(0, index) + content.substring(index + 1);
            action.onInput(this, content);
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT && index < content.length()) {
            index++;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT && index > 0) {
            index--;
        }
        time = 0;
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

    public Input(ParentElement parent, int x, int y, InputAction input) {
        this.parent = parent;
        this.action = input;
        this.client = MinecraftClient.getInstance();
        this.x = x;
        this.y = y;
    }
}