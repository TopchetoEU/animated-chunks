package me.topchetoeu.smoothchunks.gui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Element;

public final class HorizontalSection extends Section {
    private class Line {
        public Map<Element, Float> widths = new Hashtable<>();
        public List<Element> elements = new ArrayList<>();
        public float width = 0, height = 0;
    }

    private float targetWidth;

    public float getTargetWidth(float width) {
        return this.targetWidth;
    }
    public void setTargetWidth(float width) {
        this.targetWidth = width;
        recalculate();
    }

    private List<Line> getLines() {
        Line currLine = new Line();
        List<Line> lines = new ArrayList<>();

        for (Element el : children.get()) {
            var box = children.getBoundbox(el);
            var x = box.getX() + box.getWidth();
            var y = box.getY() + box.getHeight();

            if (currLine.width + x > targetWidth) {
                lines.add(currLine);
                currLine = new Line();
            }

            currLine.width += x;
            currLine.elements.add(el);
            currLine.widths.put(el, x);

            if (currLine.height < y) currLine.height = y;
        }
        lines.add(currLine);

        return lines;
    }

    private void recalculateLeft(Line line, float offsetY) {
        float currX = 0;
        for (Element el : line.elements) {
            children.offsets.put(el, new Offset(currX, offsetY));
            currX += line.widths.get(el);
        }
    }
    private void recalculateCenter(Line line, float offsetY) {
        recalculateLeft(line, offsetY);

        for (Element el : line.elements) {
            children.offsets.get(el).x += (targetWidth - line.width) / 2;
        }
    }
    private void recalculateRight(Line line, float offsetY) {
        recalculateLeft(line, offsetY);

        for (Element el : line.elements) {
            children.offsets.get(el).x += (targetWidth - line.width);
        }
    }
    private void recalculateJustified(Line line, float offsetY) {
        recalculateLeft(line, offsetY);
        int i = 0;

        if (line.elements.size() < 2) return;

        for (Element el : line.elements) {
            children.offsets.get(el).x += (targetWidth - line.width) / (line.elements.size() - 1) * i;
            i++;
        }
    }

    @Override
    protected final void recalculate() {
        width = targetWidth;
        height = title == null ? 0 : mc.textRenderer.fontHeight;

        for (Line line : getLines()) {
            if (line.width > targetWidth) {
                recalculateLeft(line, height);
            }
            else {
                switch (order) {
                    case Start: recalculateLeft(line, height); break;
                    case Middle: recalculateCenter(line, height); break;
                    case End: recalculateRight(line, height); break;
                    case Justified: recalculateJustified(line, height); break;
                }
            }
            height += line.height;
            if (width < line.width) width = line.width;
        }
    }
}
