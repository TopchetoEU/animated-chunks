package me.topchetoeu.smoothchunks.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper.Argb;

public abstract class Section extends AbstractParentElement implements Drawable, Selectable, BoundboxProvider {
    protected class Offset {
        public float x, y;

        public Offset(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public class ChildList {
        protected final Map<Element, Offset> offsets = new Hashtable<>();
        protected final Map<Element, BoundboxProvider> boundboxes = new Hashtable<>();
        protected final List<Element> children = new ArrayList<>();
        protected final List<Selectable> selectables = new ArrayList<>();
        protected final List<Drawable> drawables = new ArrayList<>();

        protected Offset getOffsetAndPos(Object element) {
            Offset of = new Offset(offsets.get(element).x, offsets.get(element).y);
            of.x += boundboxes.get(element).getX();
            of.y += boundboxes.get(element).getY();
            return of;
        }
        protected Offset getOffset(Object element) {
            return offsets.get(element);
        }
        public BoundboxProvider getBoundbox(Object element) {
            return boundboxes.get(element);
        }

        public <T extends Element & Drawable & Selectable & BoundboxProvider> T addSelectableChild(T element) {
            return addSelectableChild(element, element);
        }
        public <T extends Element & Drawable & BoundboxProvider> T addChild(T element) {
            return addChild(element, element);
        }

        public <T extends Element & Drawable & Selectable> T addSelectableChild(T element, BoundboxProvider boundbox) {
            this.boundboxes.put(element, boundbox);
            this.offsets.put(element, new Offset(0, 0));
            this.drawables.add(element);
            this.selectables.add(element);
            this.children.add(element);
            recalculate();
            return element;
        }
        public <T extends Element & Drawable> T addChild(T element, BoundboxProvider boundbox) {
            this.boundboxes.put(element, boundbox);
            this.offsets.put(element, new Offset(0, 0));
            this.drawables.add(element);
            this.children.add(element);
            recalculate();
            return element;
        }

        public List<? extends Element> get() {
            return Collections.unmodifiableList(children);
        }
    
        public void clear() {
            focusedIndex = -1;
            offsets.clear();
            boundboxes.clear();
            children.clear();
            selectables.clear();
            drawables.clear();
        }

        private ChildList() {}
    }

    public enum OrderType {
        Start,
        Middle,
        End,
        Justified,
    }

    public final ChildList children = new ChildList();
    public OrderType order = OrderType.Start;
    protected int focusedIndex = 0;
    protected float width, height;
    protected MinecraftClient mc = MinecraftClient.getInstance();
    public Text title;
    public float x, y;

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

    @Override
    public List<? extends Element> children() {
        return children.get();
    }

    @Override
    public Element getFocused() {
        if (focusedIndex < 0) return null;
        else return children().get(focusedIndex);
    }

    @Override
    public void setFocused(Element element) {
        focusedIndex = children().indexOf(element);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder var1) {
    }

    protected abstract void recalculate();

    @Override
    public SelectionType getType() {
        if (this.children.selectables.stream().anyMatch(v -> v.getType() == SelectionType.HOVERED)) {
            return SelectionType.HOVERED;
        }

        return SelectionType.NONE;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        recalculate();

        matrices.push();
        matrices.translate(x, y, getZOffset());
        
        if (title != null) {
            mc.textRenderer.draw(matrices, title, 5, 0, Argb.getArgb(255, 255, 255, 255));
            drawHorizontalLine(matrices, 0, (int)width, mc.textRenderer.fontHeight, Argb.getArgb(255, 255, 255, 255));
        }

        for (Drawable d : children.drawables) {
            Offset o = children.getOffset(d);
            
            matrices.push();
            matrices.translate(o.x, o.y, 0);
            d.render(matrices, mouseX - (int)o.x - (int)x, mouseY - (int)o.y - (int)y, delta);
            matrices.pop();
        }

        matrices.pop();
    }


    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getType() == SelectionType.HOVERED || (mouseX >= x && mouseX <= width && mouseY >= y && mouseY <= height);
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        for (Selectable element : this.children.selectables) {
            var offset = children.getOffsetAndPos(element);
            if (element.getType() != SelectionType.HOVERED || !((Element)element).isMouseOver(mouseX - offset.x, mouseY - offset.y)) continue;
            return Optional.of((Element)element);
        }
        return Optional.empty();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (Element element : this.children()) {
            var offset = children.getOffsetAndPos(element);
            if (!element.mouseScrolled(mouseX - offset.x, mouseY - offset.y, delta)) continue;
            return true;
        }
        return false;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element element : this.children()) {
            var offset = children.getOffsetAndPos(element);
            if (!element.mouseClicked(mouseX - offset.x, mouseY - offset.y, button)) continue;
            this.setFocused(element);
            if (button == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (Element element : this.children()) {
            var offset = children.getOffsetAndPos(element);
            element.mouseMoved(mouseX - offset.x, mouseY - offset.y);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);

        for (Element element : children.get()) {
            var offset = children.getOffsetAndPos(element);
            if (element.mouseReleased(mouseX - (int)offset.x, mouseY - (int)offset.y, button)) return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.getFocused() != null && this.isDragging() && button == 0) {
            var offset = children.getOffsetAndPos(getFocused());
            return this.getFocused().mouseDragged(mouseX - offset.x, mouseY - offset.y, button, deltaX, deltaY);
        }
        return false;
    }
}
