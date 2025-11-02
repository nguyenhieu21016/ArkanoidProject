package model;

public abstract class MovableObject extends GameObject {
    protected int dx;
    protected int dy;

    /**
     * Khởi tạo một đối tượng có thể di chuyển.
     * @param x tọa độ X ban đầu
     * @param y tọa độ Y ban đầu
     * @param height chiều cao của đối tượng
     * @param width chiều rộng của đối tượng
     * @param dx vận tốc theo trục X
     * @param dy vận tốc theo trục Y
     */
    public MovableObject(int x, int y, int height, int width, int dx, int dy) {
        super(x, y, height, width);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Cập nhật vị trí của đối tượng dựa trên vận tốc hiện tại.
     */
    public void move() {
        x += dx;
        y += dy;
    }

    // Getter và Setter cho vận tốc di chuyển
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
