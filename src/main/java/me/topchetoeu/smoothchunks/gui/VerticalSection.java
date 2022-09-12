package me.topchetoeu.smoothchunks.gui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Element;

public final class VerticalSection extends Section {
    private class Line {
        public Map<Element, Float> heights = new Hashtable<>();
        public List<Element> elements = new ArrayList<>();
        public float width = 0, height = 0;
    }

    private float targetHeight;

    public float getTargetHeight(float width) {
        return this.targetHeight;
    }
    public void setTargetHeight(float width) {
        this.targetHeight = width;
        recalculate();
    }

    private List<Line> getLines() {
        Line currLine = new Line();
        List<Line> lines = new ArrayList<>();

        for (Element el : children.get()) {
            var box = children.getBoundbox(el);
            var x = box.getX() + box.getWidth();
            var y = box.getY() + box.getHeight();

            if (currLine.height + y > targetHeight) {
                lines.add(currLine);
                currLine = new Line();
            }

            currLine.height += y;
            currLine.elements.add(el);
            currLine.heights.put(el, y);

            if (currLine.width < x) currLine.width = x;
        }
        lines.add(currLine);

        return lines;
    }

    private void recalculateTop(Line line, float offsetX) {
        float currY = 0;
        for (Element el : line.elements) {
            children.offsets.put(el, new Offset(offsetX, currY));
            currY += line.heights.get(el);
        }
    }
    private void recalculateCenter(Line line, float offsetX) {
        recalculateTop(line, offsetX);

        for (Element el : line.elements) {
            children.offsets.get(el).y += (targetHeight - line.height) / 2;
        }
    }
    private void recalculateBottom(Line line, float offsetX) {
        recalculateTop(line, offsetX);

        for (Element el : line.elements) {
            children.offsets.get(el).y += (targetHeight - line.height);
        }
    }
    private void recalculateJustified(Line line, float offsetX) {
        recalculateTop(line, offsetX);
        int i = 0;

        if (line.elements.size() < 2) return;

        for (Element el : line.elements) {
            children.offsets.get(el).y += (targetHeight - line.height) / (line.elements.size() - 1) * i;
            i++;
        }
    }

    @Override
    protected final void recalculate() {
        width = 0;
        height = title == null ? 0 : mc.textRenderer.fontHeight + targetHeight;

        for (Line line : getLines()) {
            if (line.height > targetHeight) {
                recalculateTop(line, width);
            }
            else {
                switch (order) {
                    case Start: recalculateTop(line, width); break;
                    case Middle: recalculateCenter(line, width); break;
                    case End: recalculateBottom(line, width); break;
                    case Justified: recalculateJustified(line, width); break;
                }
            }
            width += line.width;
            if (height < line.height) height = line.height;
        }
    }
}
