package model;

public abstract class MovableObject extends GameObject {
    protected int dx;
    protected int dy;

    public MovableObject(int x, int y, int height, int width, int dx, int dy) {
        super(x, y, height, width);
        this.dx = dx;
        this.dy = dy;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
}
