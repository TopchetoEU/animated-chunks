package me.topchetoeu.animatedchunks.gui;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import me.topchetoeu.animatedchunks.AnimatedChunks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import net.minecraft.util.math.ColorHelper.Argb;

public class ChunkPreview extends DrawableHelper implements Drawable, Element, Selectable, BoundboxProvider {
    public static interface ClickAction {
        void onClick();
    }

    public ClickAction clickAction;
    public final MinecraftClient client;
    private boolean clicked = false;
    private boolean rotating = false;
    private boolean focused = false;
    private boolean hovered = false;
    private float globalProgress = 0;
    private float duration = 0;
    private float mouseStartX, mouseStartY;
    private float rotX, rotY, scale = 1;

    public float x, y;
    public float padding = 5;
    public float width = 100, height = 100;

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
        return height;
    }

    public void click() {
        refresh();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder msgBuilder) {
        msgBuilder.put(NarrationPart.HINT, "Chunk preview");
    }

    @Override
    public SelectionType getType() {
        if (focused) return SelectionType.FOCUSED;
        if (hovered || clicked || rotating) return SelectionType.HOVERED;
        return SelectionType.NONE;
    }
    @Override
    public boolean changeFocus(boolean lookForwards) {
        focused = !focused;
        return focused;
    }

    private void refresh() {
        globalProgress = 0;
    }

    private static boolean checkZ(float z) {
        return z < 3;
    }

    private static void myFill(MatrixStack matrices, float x1, float y1, float x2, float y2, float a, float r, float g, float b) {
        if (x1 < x2) {
            float tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y1 < y2) {
            float tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        Vector4f p1 = new Vector4f(x1, y1, 0, 1);
        Vector4f p2 = new Vector4f(x1, y2, 0, 1);
        Vector4f p3 = new Vector4f(x2, y2, 0, 1);
        Vector4f p4 = new Vector4f(x2, y1, 0, 1);

        p1.transform(matrices.peek().getPositionMatrix());
        p2.transform(matrices.peek().getPositionMatrix());
        p3.transform(matrices.peek().getPositionMatrix());
        p4.transform(matrices.peek().getPositionMatrix());

        p1.multiply(1 / p1.getW());
        p2.multiply(1 / p2.getW());
        p3.multiply(1 / p3.getW());
        p4.multiply(1 / p4.getW());

        if (checkZ(p1.getZ()) && checkZ(p2.getZ()) && checkZ(p3.getZ()) && checkZ(p4.getZ())) {
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(p1.getX(), p1.getY(), p1.getZ()).color(r, g, b, a).next();
            bufferBuilder.vertex(p2.getX(), p2.getY(), p2.getZ()).color(r, g, b, a).next();
            bufferBuilder.vertex(p3.getX(), p3.getY(), p3.getZ()).color(r, g, b, a).next();
            bufferBuilder.vertex(p4.getX(), p4.getY(), p4.getZ()).color(r, g, b, a).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

    }

    private final Quaternion rotation = Quaternion.fromEulerXyzDegrees(new Vec3f(-90, 0, 0));

    private void setupDepth(MatrixStack matrices) {
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.clearDepth(-1);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, true);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.depthMask(true);
        matrices.push();
        matrices.scale(.5f, .5f, 1);
        matrices.translate(0, 0, .1f);
        if (hovered) {
            myFill(matrices, -1, -1, 1, 1, 0.1f, 1, 1, 1);
        }
        else {
            myFill(matrices, -1, -1, 1, 1, 0.004f, 0, 0, 0);
        }
        matrices.pop();
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_LESS);
        RenderSystem.enableBlend();
    }
    private void renderChunk(MatrixStack matrices, int x, int y, int n, float delta) {
        matrices.push();
        matrices.translate(x * 16 - 8, 0, y * 16 - 8);
        
        // x += n;
        // y += n;

        float progress = globalProgress / duration - (float)Math.sqrt(x * x + y * y) / (float)n / 2;
        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;
        if (progress < 0.999) {
            float _progress = AnimatedChunks.getInstance().getEaseManager().getValue().ease(progress);
            AnimatedChunks.getInstance().getAnimationManager().getValue().animate(
                _progress, matrices,
                x * 16, 0, y * 16, 0, 0, 0
            );
                // matrices.translate(0, 0, 16);
        }
        matrices.translate(0, 0, 16);
        matrices.multiply(rotation);
        myFill(matrices, 2, 1, 14, 2, progress, 1, 1, 1);
        myFill(matrices, 2, 14, 14, 15, progress, 1, 1, 1);
        myFill(matrices, 1, 1, 2, 15, progress, 1, 1, 1);
        myFill(matrices, 14, 1, 15, 15, progress, 1, 1, 1);

        matrices.pop();
    }
    private void renderChunks(MatrixStack matrices, float delta, int n) {
        duration = AnimatedChunks.getInstance().getProgressManager().getDuration();
        // globalProgress += (lastTime - (lastTime = System.nanoTime())) / -1000000000f;
        globalProgress += delta * 0.05f;
        matrices.push();

        matrices.scale(width, width, 1);
        matrices.translate(.5f, .5f, 0);
        setupDepth(matrices);

        matrices.multiplyPositionMatrix(Matrix4f.viewboxMatrix(75, 1, .00001f, 100));
        matrices.translate(0, 0, -2);
        matrices.scale(0.0625f, 0.0625f, 0.0625f);
        matrices.scale(1f / (n * 2 + 1), 1f / (n * 2 + 1), 1f / (n * 2 + 1));
        // matrices.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(150, -45, 0)));
        matrices.scale(scale, scale, scale);
        
        matrices.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(rotY + 150, 0, 0)));
        matrices.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(0, rotX - 45, 0)));
        
        // matrices.translate(-8, 0, 8);

        for (int x = -n; x <= n; x++) {
            for (int y = -n; y <= n; y++) {
                renderChunk(matrices, x, y, n, delta);
            }
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();
        RenderSystem.clearDepth(1);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, true);
        matrices.pop();
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float delta) {
        int white = Argb.getArgb(255, 255, 255, 255);
        matrices.push();
        matrices.translate(this.x, this.y, getZOffset());

        hovered = isMouseOver(x, y);

        renderChunks(matrices, delta, 5);

        drawHorizontalLine(matrices, 0, (int)getWidth() - 1, (int)getHeight() - 1, white);

        if (focused) {
            drawHorizontalLine(matrices, 0, (int)getWidth() - 1, 0, white);
            drawVerticalLine(matrices, 0, 0, (int)getHeight() - 1, white);
            drawVerticalLine(matrices, (int)getWidth() - 1, 0, (int)getHeight() - 1, white);
        }


        matrices.pop();
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        if (clicked) return true;

        x -= this.x;
        y -= this.y;

        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            click();
            clicked = true;
            hovered = true;
            return true;
        }
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isMouseOver(mouseX, mouseY)) {
            scale /= Math.pow(2, -amount / 3);
            return true;
        }
        return false;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (button == 0) {
                click();
                clicked = true;
                hovered = true;
                return true;
            }
            else if (button == 2) {
                rotating = true;
                hovered = true;
                mouseStartX = (float)mouseX;
                mouseStartY = (float)mouseY;
                return true;
            }
        }
        return false;
    }
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (rotating) {
            rotX += mouseStartX - (float)mouseX;
            rotY += mouseStartY - (float)mouseY;
            mouseStartX = (float)mouseX;
            mouseStartY = (float)mouseY;
        }
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && clicked) {
            clicked = false;
            return true;
        }
        if (button == 2 && rotating) {
            rotating = false;
            return true;
        }
        return false;
    }

    public ChunkPreview(int x, int y) {
        this.client = MinecraftClient.getInstance();
        this.x = x;
        this.y = y;
    }
    public ChunkPreview(int x, int y, int w, int h) {
        this.client = MinecraftClient.getInstance();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
}
