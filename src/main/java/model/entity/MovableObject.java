package model.entity;

public abstract class MovableObject extends GameObject {
    protected int dx;
    protected int dy;

    // Ctor: pos + size + vel
    public MovableObject(int x, int y, int width, int height, int dx, int dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    // step
    public void move() {
        setX(getX() + dx);
        setY(getY() + dy);
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
