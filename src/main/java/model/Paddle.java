package model;

public class Paddle extends MovableObject {
    private int moveSpeed;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    /**
     * Khởi tạo Paddle với vị trí, kích thước và tốc độ di chuyển.
     * @param x tọa độ X
     * @param y tọa độ Y
     * @param width chiều rộng
     * @param height chiều cao
     * @param moveSpeed tốc độ di chuyển
     */
    public Paddle(int x, int y, int width, int height, int moveSpeed) {
        super(x, y, width, height, 0, 0);
        this.moveSpeed = moveSpeed;
    }

    private void moveLeft() {
        x -= moveSpeed;
    }

    private void moveRight() {
        x += moveSpeed;
    }

    /**
     * Cập nhật vị trí paddle theo hướng di chuyển và kiểm tra va chạm cạnh màn hình.
     * @param screenWidth độ rộng của màn hình
     */
    public void update(int screenWidth) {
        if (movingRight) {
            moveRight();
        }
        if (movingLeft) {
            moveLeft();
        }
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }

    public void setMovingLeft(boolean status) {
        movingLeft = status;
    }

    public void setMovingRight(boolean status) {
        movingRight = status;
    }

    @Override
    public void update() {
    }
}
