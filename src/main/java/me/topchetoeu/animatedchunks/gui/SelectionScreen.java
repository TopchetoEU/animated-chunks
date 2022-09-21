package me.topchetoeu.animatedchunks.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import me.topchetoeu.animatedchunks.Descriptor;
import me.topchetoeu.animatedchunks.Manager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper.Argb;

public class SelectionScreen<T> extends Screen {
    public static interface SelectAction<T> {
        void onSelect(Descriptor<T> desc);
    }

    public final Screen parent;
    public final Manager<T> manager;
    public SelectAction<T> selectAction;
    private Descriptor<T> selectedElement;
    private Descriptor<T> hoveredElement = null;

    public Descriptor<T> getSelected() {
        return selectedElement;
    }

    public static float drawWarpedText(TextRenderer textRenderer, MatrixStack matrices, Text text, int x, int y, int width) {
        var lines = textRenderer.wrapLines(text, width);
        float offset = 0;

        for (var line : lines) {
            textRenderer.draw(matrices, line, x, y + offset, 0xFFFFFFFF);
            offset += textRenderer.fontHeight;
        }

        return offset;
    }

    private float renderElement(MatrixStack matrices, int mouseX, int mouseY, Descriptor<T> element) {
        float y = 0;
        matrices.push();
        matrices.translate(20, 5, 0);
        textRenderer.draw(matrices, Text.of(element.getDisplayNameOrDefault()).copy().formatted(Formatting.BOLD), 0, y, 0xFFFFFFFF);
        y += textRenderer.fontHeight + 3;
        textRenderer.draw(matrices, Text.of("Author: " + element.getAuthorOrDefault()), 0, y, 0xFFFFFFFF);
        y += textRenderer.fontHeight + 2;
        y += drawWarpedText(textRenderer, matrices, Text.of(element.getDescriptionOrDefault()).copy().formatted(Formatting.ITALIC), 0, (int)y, width - 40);
        y += 5;
        matrices.pop();

        return y;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (parent == null) renderBackground(matrices);
        else parent.render(matrices, Integer.MIN_VALUE, Integer.MIN_VALUE, delta);

        fill(matrices, 0, 0, width, height, Argb.getArgb(220, 0, 0, 0));

        Map<Descriptor<T>, Integer> heights = new LinkedHashMap<>();

        matrices.push();
        var currDesc = new Descriptor<T>(manager.getValue(), manager.get().getName())
            .author(manager.get().getAuthor())
            .description(manager.get().getDescription())
            .displayName(manager.get().getDisplayNameOrDefault() + " (currently selected)");
            float offset = renderElement(matrices, mouseX, mouseY, currDesc);
        heights.put(currDesc, (int)offset);
        matrices.translate(0, offset, 0);
        for (Descriptor<T> desc : manager.getAll()) {
            offset = renderElement(matrices, mouseX, mouseY, desc);
            heights.put(desc, (int)offset);
            matrices.translate(0, offset, 0);
        }
        matrices.pop();

        int y1 = 5;

        for (var pair : heights.entrySet()) {
            int y2 = y1 + pair.getValue();

            if (mouseY >= y1 - 3 && mouseY <= y2 - 5 && mouseX >= 15 && mouseX <= width - 15) {
                hoveredElement = pair.getKey();
                drawHorizontalLine(matrices, 15, width - 15, (int)y1 - 3, 0xFFFFFFFF);
                drawHorizontalLine(matrices, 15, width - 15, (int)y2 - 5, 0xFFFFFFFF);
                drawVerticalLine(matrices, 15, y1 - 3, y2 - 5, 0xFFFFFFFF);
                drawVerticalLine(matrices, width - 15, y1 - 3, y2 - 5, 0xFFFFFFFF);
                break;
            }

            y1 = y2;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        parent.init(client, width, height);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredElement != null) {
            selectAction.onSelect(hoveredElement);
            selectedElement = hoveredElement;
            close();
            return true;
        }
        return false;
    }

    public SelectionScreen(Screen parent, Manager<T> manager, SelectAction<T> selectAction) {
        super(Text.of("Selection"));

        this.manager = manager;
        this.parent = parent;
        this.selectAction = selectAction;
    }
}
