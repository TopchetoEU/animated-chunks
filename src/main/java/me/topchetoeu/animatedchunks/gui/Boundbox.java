package me.topchetoeu.animatedchunks.gui;

public class Boundbox implements BoundboxProvider {
    public float x, y, width, height;

    @Override
    public float getX() {
        return x;
    }
    @Override
    public float getY() {
        return y;
    }
    @Override
    public float getWidth() {
        return width;
    }
    @Override
    public float getHeight() {
        return height;
    }

    public Boundbox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
