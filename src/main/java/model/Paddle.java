package model;

import java.awt.Color;
import java.awt.Graphics;

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

    // Di chuyển paddle sang trái
    public void moveLeft() {
        x -= moveSpeed;
    }

    // Di chuyển paddle sang phải
    public void moveRight() {
        x += moveSpeed;
    }

    /**
     * Cập nhật vị trí paddle theo hướng di chuyển và kiểm tra va chạm cạnh màn hình.
     * @param screenWidth độ rộng của màn hình
     */
    public void update(int screenWidth) {
        // Nếu đang di chuyển sang phải
        if (movingRight) {
            moveRight();
        }
        // Nếu đang di chuyển sang trái
        if (movingLeft) {
            moveLeft();
        }
        // Giới hạn paddle không vượt ra ngoài bên trái
        if (x < 0) {
            x = 0;
        }
        // Giới hạn paddle không vượt ra ngoài bên phải
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }

    /**
     * Ghi đè phương thức update (hiện không sử dụng, để tương thích với GameObject).
     */
    @Override
    public void update() {
    }

    /**
     * Vẽ paddle lên màn hình bằng màu xanh lam.
     * @param g đối tượng Graphics dùng để vẽ
     */
    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    // Setter điều khiển hướng di chuyển của paddle
    public void setMovingLeft(boolean status) {
        movingLeft = status;
    }

    // Setter điều khiển hướng di chuyển của paddle
    public void setMovingRight(boolean status) {
        movingRight = status;
    }
}
