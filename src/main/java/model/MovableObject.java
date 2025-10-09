package model;

public abstract class MovableObject extends GameObject {
    protected int dx;
    protected int dy;

    /**
     * Constructor để khởi tạo một MovableObject.
     * @param x x
     * @param y y
     * @param height chiều cao
     * @param width chiều rộng
     * @param dx tốc độ di chuyển theo trục x
     * @param dy tốc độ di chuyển theo trục y
     */
    public MovableObject(int x, int y, int height, int width, int dx, int dy) {
        super(x, y, height, width);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Method giúp di chuyển.
     */
    public void move() {
        x += dx;
        y += dy;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
}
