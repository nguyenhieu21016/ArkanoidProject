package model.entity;

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
        setX(getX() - moveSpeed);
    }

    private void moveRight() {
        setX(getX() + moveSpeed);
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
        if (getX() < 0) {
            setX(0);
        }
        if (getX() + getWidth() > screenWidth) {
            setX(screenWidth - getWidth());
        }
    }

    public void setMovingLeft(boolean status) {
        movingLeft = status;
    }

    public void setMovingRight(boolean status) {
        movingRight = status;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    @Override
    public void update() {
    }
}
